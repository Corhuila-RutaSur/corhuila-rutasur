import { CurrencyPipe, DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { catchError, forkJoin, map, of, switchMap } from 'rxjs';
import { NavbarPublicaComponent } from '../../../../shared/components/navbar-publica/navbar-publica.component';
import { ModalComponent } from '../../../../shared/components/modal/modal.component';
import { Reserva } from '../../../../core/models/reserva.model';
import { NotificacionService } from '../../../../core/services/notificacion.service';
import { ReservaService } from '../../../../core/services/reserva.service';
import { SesionService } from '../../../../core/services/sesion.service';
import { PlanificacionService } from '../../../../core/services/planificacion.service';
import { ExperienciaService } from '../../../../core/services/experiencia.service';

/** Muestra al cliente las reservas creadas con su estado e importe histórico. */
@Component({ selector: 'rs-mis-reservas', imports: [CurrencyPipe, DatePipe, ModalComponent, NavbarPublicaComponent, RouterLink], templateUrl: './mis-reservas.component.html', styleUrl: './mis-reservas.component.scss', changeDetection: ChangeDetectionStrategy.OnPush })
export class MisReservasComponent {
  private readonly reservasApi = inject(ReservaService);
  private readonly sesion = inject(SesionService);
  private readonly aviso = inject(NotificacionService);
  private readonly planificacion = inject(PlanificacionService);
  private readonly experiencias = inject(ExperienciaService);
  readonly reservas = signal<Reserva[]>([]);
  readonly cargando = signal(true);
  readonly cancelandoId = signal<number | null>(null);
  readonly pagandoId = signal<number | null>(null);
  readonly reservaParaCancelar = signal<Reserva | null>(null);
  readonly reservaParaPagar = signal<Reserva | null>(null);
  constructor() { this.cargar(); }
  cargar(): void { const usuarioId = this.sesion.usuarioId(); if (!usuarioId) { this.cargando.set(false); return; } this.reservasApi.listarPorUsuario(usuarioId).pipe(switchMap(respuesta => forkJoin((respuesta.data ?? []).map(reserva => this.planificacion.salidaPublica(reserva.salidaExperienciaId).pipe(switchMap(salida => this.experiencias.consultar(salida.data!.experienciaId).pipe(map(experiencia => ({...reserva,experienciaId: experiencia.data?.id,experienciaNombre: experiencia.data?.nombre,experienciaDestino: experiencia.data?.destino})))),catchError(() => of(reserva))))))).subscribe({ next: respuesta => this.reservas.set(respuesta), error: error => this.aviso.mostrar(error.error?.mensaje ?? 'No fue posible consultar tus reservas.', 'error'), complete: () => this.cargando.set(false) }); }
  solicitarCancelacion(reserva: Reserva): void { this.reservaParaCancelar.set(reserva); }
  cerrarModalCancelacion(): void { if (!this.cancelandoId()) this.reservaParaCancelar.set(null); }
  confirmarCancelacion(): void { const reserva = this.reservaParaCancelar(); if (!reserva) return; this.cancelandoId.set(reserva.id); this.reservasApi.cancelar(reserva.id).subscribe({ next: respuesta => { this.reservas.update(listado => listado.map(item => item.id === reserva.id ? { ...item, estado: 'CANCELADA' } : item)); this.aviso.mostrar(respuesta.mensaje || 'Reserva cancelada.', 'exito'); }, error: error => this.aviso.mostrar(error.error?.mensaje ?? 'No fue posible cancelar la reserva.', 'error'), complete: () => { this.cancelandoId.set(null); this.reservaParaCancelar.set(null); } }); }
  solicitarPago(reserva: Reserva): void { this.reservaParaPagar.set(reserva); }
  cerrarModalPago(): void { if (!this.pagandoId()) this.reservaParaPagar.set(null); }
  confirmarPago(): void { const reserva = this.reservaParaPagar(); if (!reserva) return; this.pagandoId.set(reserva.id); this.reservasApi.pagarSimulado(reserva.id).subscribe({ next: respuesta => { this.reservas.update(listado => listado.map(item => item.id === reserva.id ? { ...item, estado: 'CONFIRMADA' } : item)); this.aviso.mostrar(respuesta.mensaje || 'Pago simulado aprobado.', 'exito'); }, error: error => this.aviso.mostrar(error.error?.mensaje ?? 'No fue posible procesar el pago.', 'error'), complete: () => { this.pagandoId.set(null); this.reservaParaPagar.set(null); } }); }
  descripcionPago(): string { const reserva = this.reservaParaPagar(); return reserva ? `Vas a simular el pago de la reserva ${reserva.codigo} por ${new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 }).format(reserva.total)}.` : ''; }
  puedeCancelar(reserva: Reserva): boolean { return reserva.estado === 'PENDIENTE' || reserva.estado === 'CONFIRMADA'; }
}
