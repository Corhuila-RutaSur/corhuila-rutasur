import { CurrencyPipe, DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { NavbarPublicaComponent } from '../../../../shared/components/navbar-publica/navbar-publica.component';
import { DisponibilidadSalida } from '../../../../core/models/disponibilidad-salida.model';
import { Experiencia } from '../../../../core/models/experiencia.model';
import { ParticipanteReservaSolicitud } from '../../../../core/models/reserva.model';
import { ArchivoPublicoService } from '../../../../core/services/archivo-publico.service';
import { DisponibilidadSalidaService } from '../../../../core/services/disponibilidad-salida.service';
import { ExperienciaService } from '../../../../core/services/experiencia.service';
import { NotificacionService } from '../../../../core/services/notificacion.service';
import { ReservaService } from '../../../../core/services/reserva.service';
import { SesionService } from '../../../../core/services/sesion.service';

/** Presenta el detalle público y arma una reserva con la disponibilidad CQRS de V2. */
@Component({ selector: 'rs-detalle-experiencia', imports: [CurrencyPipe, DatePipe, FormsModule, NavbarPublicaComponent, RouterLink], templateUrl: './detalle-experiencia.component.html', styleUrl: './detalle-experiencia.component.scss', changeDetection: ChangeDetectionStrategy.OnPush })
export class DetalleExperienciaComponent {
  private readonly ruta = inject(ActivatedRoute);
  private readonly experienciasApi = inject(ExperienciaService);
  private readonly disponibilidadApi = inject(DisponibilidadSalidaService);
  private readonly reservaApi = inject(ReservaService);
  private readonly aviso = inject(NotificacionService);
  private readonly archivos = inject(ArchivoPublicoService);
  readonly sesion = inject(SesionService);
  private readonly router = inject(Router);
  readonly experiencia = signal<Experiencia | null>(null);
  readonly disponibilidad = signal<DisponibilidadSalida | null>(null);
  readonly salidaSeleccionadaId = signal<string | null>(null);
  readonly serviciosSeleccionados = signal<Set<string>>(new Set());
  readonly actividadesSeleccionadas = signal<Set<string>>(new Set());
  readonly cargandoOpciones = signal(true);
  readonly mostrandoFormularioReserva = signal(false);
  readonly guardandoReserva = signal(false);
  participantes: ParticipanteReservaSolicitud[] = [this.nuevoParticipante(true)];

  constructor() {
    const experienciaId = this.ruta.snapshot.paramMap.get('id');
    if (!experienciaId) { this.cargandoOpciones.set(false); return; }
    this.experienciasApi.consultar(experienciaId).subscribe({
      next: respuesta => {
        const experiencia = respuesta.data ?? null;
        this.experiencia.set(experiencia);
        const salida = experiencia?.salidas?.find(item => item.estado === 'PROGRAMADA' && item.cuposDisponibles > 0) ?? experiencia?.salidas?.[0];
        if (salida) this.seleccionarSalida(salida.id); else this.cargandoOpciones.set(false);
      },
      error: error => { this.cargandoOpciones.set(false); this.aviso.mostrar(error.error?.mensaje ?? 'No fue posible consultar esta experiencia.', 'error'); }
    });
  }

  /** Convierte la ruta devuelta por Catálogo en una URL pública de navegador. */
  urlImagen(ruta: string | undefined): string { return this.archivos.resolver(ruta, '/assets/mascota/tati-bienvenida.png'); }

  /** Consulta la proyección CQRS de precios, cupos y estado de la salida seleccionada. */
  seleccionarSalida(salidaId: string): void {
    this.salidaSeleccionadaId.set(salidaId);
    this.serviciosSeleccionados.set(new Set()); this.actividadesSeleccionadas.set(new Set()); this.cargandoOpciones.set(true);
    this.disponibilidadApi.consultar(salidaId).subscribe({
      next: respuesta => this.disponibilidad.set(respuesta.data ?? null),
      error: error => { this.disponibilidad.set(null); this.aviso.mostrar(error.error?.mensaje ?? 'No fue posible cargar la disponibilidad.', 'error'); },
      complete: () => this.cargandoOpciones.set(false)
    });
  }

  alternarServicio(programacionId: string): void { this.serviciosSeleccionados.update(seleccion => this.alternarSeleccion(seleccion, programacionId)); }
  alternarActividad(programacionId: string): void { this.actividadesSeleccionadas.update(seleccion => this.alternarSeleccion(seleccion, programacionId)); }

  /** Total estimado individual según las opciones marcadas. */
  totalPorPersona(): number {
    const salida = this.disponibilidad(); if (!salida) return 0;
    const servicios = salida.servicios.filter(item => this.serviciosSeleccionados().has(item.programacionId)).reduce((total, item) => total + Number(item.precio), 0);
    const actividades = salida.actividades.filter(item => this.actividadesSeleccionadas().has(item.programacionId)).reduce((total, item) => total + Number(item.precio), 0);
    return servicios + actividades;
  }
  totalEstimado(): number { return this.totalPorPersona() * this.participantes.length; }

  continuarReserva(): void {
    if (!this.sesion.sesion()) { this.router.navigate(['/iniciar-sesion'], { queryParams: { retorno: this.router.url } }); return; }
    if (!this.serviciosSeleccionados().size && !this.actividadesSeleccionadas().size) { this.aviso.mostrar('Selecciona al menos un servicio o una actividad.', 'advertencia'); return; }
    this.mostrandoFormularioReserva.set(true);
  }
  agregarParticipante(): void { this.participantes = [...this.participantes, this.nuevoParticipante(false)]; }
  eliminarParticipante(indice: number): void { if (this.participantes.length > 1) this.participantes = this.participantes.filter((_, posicion) => posicion !== indice); }
  marcarResponsable(indice: number): void { this.participantes.forEach((participante, posicion) => participante.responsable = posicion === indice); }

  /** Crea la reserva V2 con UUID de salida y UUID de sus programaciones seleccionadas. */
  crearReserva(): void {
    const usuarioIdExterno = this.sesion.usuarioId(); const salidaId = this.salidaSeleccionadaId();
    if (!usuarioIdExterno || !salidaId || !this.participantesValidos()) return;
    this.guardandoReserva.set(true);
    this.reservaApi.crear({ usuarioIdExterno, salidaId, cantidadParticipantes: this.participantes.length,
      participantes: this.participantes.map(({ tipoDocumento, numeroDocumento, nombres, apellidos, responsable }) => ({ tipoDocumento, numeroDocumento, nombres, apellidos, responsable })),
      programacionesServicio: [...this.serviciosSeleccionados()], programacionesActividad: [...this.actividadesSeleccionadas()] }).subscribe({
      next: respuesta => { this.aviso.mostrar(respuesta.mensaje || `Reserva ${respuesta.data?.codigo ?? ''} creada.`, 'exito'); this.mostrandoFormularioReserva.set(false); this.router.navigate(['/mis-reservas']); },
      error: error => this.aviso.mostrar(error.error?.mensaje ?? 'No fue posible crear la reserva.', 'error'),
      complete: () => this.guardandoReserva.set(false)
    });
  }

  private alternarSeleccion(actual: Set<string>, programacionId: string): Set<string> { const seleccion = new Set(actual); seleccion.has(programacionId) ? seleccion.delete(programacionId) : seleccion.add(programacionId); return seleccion; }
  private participantesValidos(): boolean {
    if (this.participantes.some(item => !item.numeroDocumento.trim() || !item.nombres.trim() || !item.apellidos.trim())) { this.aviso.mostrar('Completa documento, nombres y apellidos de cada participante.', 'advertencia'); return false; }
    if (this.participantes.filter(item => item.responsable).length !== 1) { this.aviso.mostrar('Selecciona exactamente un responsable para la reserva.', 'advertencia'); return false; }
    return true;
  }
  private nuevoParticipante(responsable: boolean): ParticipanteReservaSolicitud { return { tipoDocumento: 'CC', numeroDocumento: '', nombres: '', apellidos: '', responsable }; }
}
