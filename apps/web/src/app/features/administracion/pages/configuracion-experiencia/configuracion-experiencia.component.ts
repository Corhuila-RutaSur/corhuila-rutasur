import { DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Experiencia } from '../../../../core/models/experiencia.model';
import { SalidaExperiencia, ServicioExperiencia, TipoServicio } from '../../../../core/models/planificacion.model';
import { ExperienciaService } from '../../../../core/services/experiencia.service';
import { NotificacionService } from '../../../../core/services/notificacion.service';
import { PlanificacionService } from '../../../../core/services/planificacion.service';

/** Configura servicios comerciales y salidas V2 sin usar las rutas del monolito V1. */
@Component({ selector: 'rs-configuracion-experiencia', imports: [DatePipe, FormsModule, RouterLink], templateUrl: './configuracion-experiencia.component.html', styleUrl: './configuracion-experiencia.component.scss', changeDetection: ChangeDetectionStrategy.OnPush })
export class ConfiguracionExperienciaComponent {
  private readonly ruta = inject(ActivatedRoute);
  private readonly planificacionApi = inject(PlanificacionService);
  private readonly experienciasApi = inject(ExperienciaService);
  private readonly aviso = inject(NotificacionService);
  readonly experienciaId = this.ruta.snapshot.paramMap.get('id') ?? '';
  readonly experiencia = signal<Experiencia | null>(null);
  readonly servicios = signal<ServicioExperiencia[]>([]);
  readonly salidas = signal<SalidaExperiencia[]>([]);
  readonly salidaSeleccionada = signal<string | null>(null);
  readonly guardandoServicio = signal(false);
  readonly guardandoSalida = signal(false);
  readonly guardandoProgramacion = signal(false);
  servicio: Pick<ServicioExperiencia, 'tipo' | 'nombre' | 'descripcion'> = { tipo: 'ACTIVIDAD', nombre: '', descripcion: '' };
  salida = { fechaHoraInicio: '', cuposTotales: 1 };
  precio = 0;
  cupos = 1;
  servicioProgramado: string | null = null;

  constructor() { this.cargar(); }

  /** Recupera las entidades dueñas de Catálogo y Reservas, filtrando salidas por UUID de experiencia. */
  cargar(): void {
    if (!this.experienciaId) return;
    this.experienciasApi.consultarAdministracion(this.experienciaId).subscribe({ next: respuesta => this.experiencia.set(respuesta.data ?? null), error: error => this.aviso.mostrar(error.error?.mensaje ?? 'No fue posible consultar la experiencia.', 'error') });
    this.planificacionApi.servicios(this.experienciaId).subscribe({ next: respuesta => this.servicios.set(respuesta.data ?? []), error: error => this.aviso.mostrar(error.error?.mensaje ?? 'No fue posible consultar los servicios.', 'error') });
    this.planificacionApi.salidas().subscribe({ next: respuesta => this.salidas.set((respuesta.data ?? []).filter(salida => salida.experienciaIdExterna === this.experienciaId)), error: error => this.aviso.mostrar(error.error?.mensaje ?? 'No fue posible consultar las salidas.', 'error') });
  }

  crearServicio(): void {
    if (!this.servicio.nombre.trim()) { this.aviso.mostrar('Indica el nombre del servicio.', 'advertencia'); return; }
    this.guardandoServicio.set(true);
    this.planificacionApi.crearServicio(this.experienciaId, { ...this.servicio, nombre: this.servicio.nombre.trim(), descripcion: this.servicio.descripcion?.trim() || undefined }).subscribe({
      next: respuesta => { this.aviso.mostrar(respuesta.mensaje || 'Servicio creado.', 'exito'); this.servicio = { tipo: 'ACTIVIDAD', nombre: '', descripcion: '' }; this.cargar(); },
      error: error => this.aviso.mostrar(error.error?.mensaje ?? 'No fue posible crear el servicio.', 'error'), complete: () => this.guardandoServicio.set(false)
    });
  }

  crearSalida(): void {
    const experiencia = this.experiencia();
    if (!experiencia || !this.salida.fechaHoraInicio || this.salida.cuposTotales < 1) { this.aviso.mostrar('Indica una fecha futura y al menos un cupo.', 'advertencia'); return; }
    this.guardandoSalida.set(true);
    this.planificacionApi.crearSalida({ experienciaIdExterna: this.experienciaId, experienciaNombre: experiencia.nombre, fechaHoraInicio: new Date(this.salida.fechaHoraInicio).toISOString(), cuposTotales: this.salida.cuposTotales }).subscribe({
      next: respuesta => { this.aviso.mostrar(respuesta.mensaje || 'Salida creada.', 'exito'); this.salida = { fechaHoraInicio: '', cuposTotales: 1 }; this.cargar(); },
      error: error => this.aviso.mostrar(error.error?.mensaje ?? 'No fue posible crear la salida.', 'error'), complete: () => this.guardandoSalida.set(false)
    });
  }

  seleccionarSalida(id: string): void { this.salidaSeleccionada.set(id); this.cupos = this.salidas().find(salida => salida.id === id)?.cuposDisponibles ?? 1; }

  /** Habilita un servicio de Catálogo en la salida seleccionada con precio y cupos explícitos. */
  programar(): void {
    const salidaId = this.salidaSeleccionada();
    const servicio = this.servicios().find(item => item.id === this.servicioProgramado);
    if (!salidaId || !servicio || this.precio < 0 || this.cupos < 1) { this.aviso.mostrar('Selecciona servicio e indica precio y cupos válidos.', 'advertencia'); return; }
    this.guardandoProgramacion.set(true);
    this.planificacionApi.crearProgramacion(salidaId, { servicioIdExterno: servicio.id, nombre: servicio.nombre, precio: this.precio, cuposTotales: this.cupos }).subscribe({
      next: respuesta => { this.aviso.mostrar(respuesta.mensaje || 'Servicio habilitado para la salida.', 'exito'); this.servicioProgramado = null; this.precio = 0; },
      error: error => this.aviso.mostrar(error.error?.mensaje ?? 'No fue posible programar el servicio.', 'error'), complete: () => this.guardandoProgramacion.set(false)
    });
  }
}
