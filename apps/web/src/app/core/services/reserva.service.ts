import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_V2 } from '../config/api.config';
import { Reserva, ReservaCreacionSolicitud, ReservaCreada } from '../models/reserva.model';
import { RespuestaGeneral } from '../models/respuesta-general.model';

/** Consume las operaciones protegidas de reserva de experiencias. */
@Injectable({ providedIn: 'root' })
export class ReservaService {
  private readonly http = inject(HttpClient);
  private readonly api = inject(API_V2);

  /** Registra una reserva con sus participantes y servicios seleccionados. */
  crear(solicitud: ReservaCreacionSolicitud): Observable<RespuestaGeneral<ReservaCreada>> {
    return this.http.post<RespuestaGeneral<ReservaCreada>>(`${this.api.reservas}/reservas`, solicitud);
  }

  /** Consulta el historial de reservas del usuario autenticado. */
  listarPorUsuario(usuarioId: string): Observable<RespuestaGeneral<Reserva[]>> {
    return this.http.get<RespuestaGeneral<Reserva[]>>(`${this.api.reservas}/reservas/usuarios/${usuarioId}`);
  }

  /** Cancela una reserva pendiente sin borrar su historial. */
  cancelar(reservaId: string, usuarioId: string): Observable<RespuestaGeneral<ReservaCreada>> {
    return this.http.delete<RespuestaGeneral<ReservaCreada>>(`${this.api.reservas}/reservas/${reservaId}`, { headers: { 'X-Usuario-Id': usuarioId } });
  }

  pagarSimulado(reservaId: string, monto: number): Observable<RespuestaGeneral<unknown>> { return this.http.post<RespuestaGeneral<unknown>>(`${this.api.pagos}/pagos`, { reservaIdExterna: reservaId, monto, resultado: 'APROBADO' }, { headers: { 'X-Correlation-Id': crypto.randomUUID() } }); }
}
