import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { URL_API } from '../config/api.config';
import { Reserva, ReservaCreacionSolicitud, ReservaCreada } from '../models/reserva.model';
import { RespuestaGeneral } from '../models/respuesta-general.model';

/** Consume las operaciones protegidas de reserva de experiencias. */
@Injectable({ providedIn: 'root' })
export class ReservaService {
  private readonly http = inject(HttpClient);
  private readonly url = inject(URL_API);

  /** Registra una reserva con sus participantes y servicios seleccionados. */
  crear(solicitud: ReservaCreacionSolicitud): Observable<RespuestaGeneral<ReservaCreada>> {
    return this.http.post<RespuestaGeneral<ReservaCreada>>(`${this.url}/reservas`, solicitud);
  }

  /** Consulta el historial de reservas del usuario autenticado. */
  listarPorUsuario(usuarioId: number): Observable<RespuestaGeneral<Reserva[]>> {
    return this.http.get<RespuestaGeneral<Reserva[]>>(`${this.url}/usuarios/${usuarioId}/reservas`);
  }

  /** Cancela una reserva pendiente sin borrar su historial. */
  cancelar(reservaId: number): Observable<RespuestaGeneral<void>> {
    return this.http.delete<RespuestaGeneral<void>>(`${this.url}/reservas/${reservaId}`);
  }

  pagarSimulado(reservaId: number): Observable<RespuestaGeneral<unknown>> { return this.http.post<RespuestaGeneral<unknown>>(`${this.url}/reservas/${reservaId}/pagos/simulado`, { metodoPago: 'TARJETA_SIMULADA' }); }
}
