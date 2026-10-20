import { CurrencyPipe, DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { NavbarPublicaComponent } from '../../../../shared/components/navbar-publica/navbar-publica.component';
import { Experiencia } from '../../../../core/models/experiencia.model';
import { ProgramacionServicio, SalidaExperiencia, ServicioExperiencia } from '../../../../core/models/planificacion.model';
import { ArchivoPublicoService } from '../../../../core/services/archivo-publico.service';
import { ExperienciaService } from '../../../../core/services/experiencia.service';
import { PlanificacionService } from '../../../../core/services/planificacion.service';
import { ReservaService } from '../../../../core/services/reserva.service';
import { SesionService } from '../../../../core/services/sesion.service';
import { NotificacionService } from '../../../../core/services/notificacion.service';
import { ParticipanteReservaSolicitud } from '../../../../core/models/reserva.model';

/** Presenta el detalle público y permite armar un paquete para una salida. */
@Component({
  selector: 'rs-detalle-experiencia',
  imports: [CurrencyPipe, DatePipe, FormsModule, NavbarPublicaComponent, RouterLink],
  templateUrl: './detalle-experiencia.component.html',
  styleUrl: './detalle-experiencia.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class DetalleExperienciaComponent {
  private readonly ruta = inject(ActivatedRoute);
  private readonly experienciasApi = inject(ExperienciaService);
  private readonly planificacionApi = inject(PlanificacionService);
  private readonly reservaApi = inject(ReservaService);
  private readonly aviso = inject(NotificacionService);
  private readonly archivos = inject(ArchivoPublicoService);
  readonly sesion = inject(SesionService);
  private readonly router = inject(Router);

  readonly experiencia = signal<Experiencia | null>(null);
  readonly servicios = signal<ServicioExperiencia[]>([]);
  readonly salidas = signal<SalidaExperiencia[]>([]);
  readonly programaciones = signal<ProgramacionServicio[]>([]);
  readonly salidaSeleccionadaId = signal<number | null>(null);
  readonly programacionesSeleccionadas = signal<Set<number>>(new Set());
  readonly cargandoOpciones = signal(true);
  readonly mostrandoFormularioReserva = signal(false);
  readonly guardandoReserva = signal(false);
  participantes: ParticipanteReservaSolicitud[] = [this.nuevoParticipante(true)];
  observacionesReserva = '';

  constructor() {
    const id = Number(this.ruta.snapshot.paramMap.get('id'));
    forkJoin({
      experiencia: this.experienciasApi.consultar(id),
      servicios: this.planificacionApi.serviciosPublicos(id),
      salidas: this.planificacionApi.salidasPublicas(id)
    }).subscribe({
      next: respuesta => {
        this.experiencia.set(respuesta.experiencia.data ?? null);
        this.servicios.set(respuesta.servicios.data ?? []);
        const salidas = respuesta.salidas.data ?? [];
        this.salidas.set(salidas);
        if (salidas.length) this.seleccionarSalida(salidas[0].id);
        else this.cargandoOpciones.set(false);
      },
      error: () => this.cargandoOpciones.set(false)
    });
  }

  /** Convierte una ruta relativa del backend en una URL usable por el navegador. */
  urlImagen(ruta: string | undefined): string {
    return this.archivos.resolver(ruta, '/assets/mascota/tati-bienvenida.png');
  }

  /** Carga la oferta y precios correspondientes a la salida elegida. */
  seleccionarSalida(salidaId: number): void {
    this.salidaSeleccionadaId.set(salidaId);
    this.programacionesSeleccionadas.set(new Set());
    this.cargandoOpciones.set(true);
    this.planificacionApi.programacionesPublicas(salidaId).subscribe({
      next: respuesta => this.programaciones.set(respuesta.data ?? []),
      error: () => this.programaciones.set([]),
      complete: () => this.cargandoOpciones.set(false)
    });
  }

  /** Alterna un servicio programado dentro del paquete que está armando el visitante. */
  alternarServicio(programacionId: number): void {
    const seleccion = new Set(this.programacionesSeleccionadas());
    seleccion.has(programacionId) ? seleccion.delete(programacionId) : seleccion.add(programacionId);
    this.programacionesSeleccionadas.set(seleccion);
  }

  /** Busca la información de catálogo asociada a una programación de salida. */
  servicioDe(programacion: ProgramacionServicio): ServicioExperiencia | undefined {
    return this.servicios().find(servicio => servicio.id === programacion.servicioExperienciaId);
  }

  /** Calcula el valor individual de los servicios que el visitante ha incluido. */
  totalSeleccionado(): number {
    return this.programaciones().filter(programacion => this.programacionesSeleccionadas().has(programacion.id))
      .reduce((total, programacion) => total + Number(programacion.precioUnitario), 0);
  }

  /** Dirige al inicio de sesión antes de iniciar una reserva protegida. */
  continuarReserva(): void {
    if (!this.sesion.sesion()) {
      this.router.navigate(['/iniciar-sesion'], { queryParams: { retorno: this.router.url } });
      return;
    }
    this.mostrandoFormularioReserva.set(true);
  }

  /** Añade un nuevo cupo con la misma selección de servicios del paquete. */
  agregarParticipante(): void {
    this.participantes = [...this.participantes, this.nuevoParticipante(false)];
  }

  /** Elimina un participante secundario sin permitir que la reserva quede vacía. */
  eliminarParticipante(indice: number): void {
    if (this.participantes.length === 1) return;
    this.participantes = this.participantes.filter((_, posicion) => posicion !== indice);
  }

  /** Marca a un único participante como responsable de la reserva. */
  marcarResponsable(indice: number): void {
    this.participantes.forEach((participante, posicion) => participante.responsable = posicion === indice);
  }

  /** Persiste la reserva con todos los servicios seleccionados para cada participante. */
  crearReserva(): void {
    const usuarioId = this.sesion.usuarioId();
    const salidaId = this.salidaSeleccionadaId();
    const servicios = [...this.programacionesSeleccionadas()];
    if (!usuarioId || !salidaId || !servicios.length) return;
    if (this.participantes.filter(participante => participante.responsable).length !== 1) {
      this.aviso.mostrar('Selecciona exactamente un responsable para la reserva.', 'advertencia');
      return;
    }
    this.guardandoReserva.set(true);
    this.reservaApi.crear({ usuarioId, salidaExperienciaId: salidaId, programacionesServicioIds: servicios,
      participantes: this.participantes, observaciones: this.observacionesReserva.trim() || undefined }).subscribe({
      next: respuesta => {
        this.aviso.mostrar(respuesta.mensaje || `Reserva ${respuesta.data?.codigo ?? ''} creada.`, 'exito');
        this.mostrandoFormularioReserva.set(false);
        this.programacionesSeleccionadas.set(new Set());
      },
      error: error => this.aviso.mostrar(error.error?.mensaje ?? 'No fue posible crear la reserva.', 'error'),
      complete: () => this.guardandoReserva.set(false)
    });
  }

  /** Inicializa un formulario de participante sin datos personales preasignados. */
  private nuevoParticipante(responsable: boolean): ParticipanteReservaSolicitud {
    return { tipoDocumento: 'CC', numeroDocumento: '', nombres: '', apellidos: '', correo: '', telefono: '', responsable };
  }
}
