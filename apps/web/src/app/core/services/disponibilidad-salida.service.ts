import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_V2 } from '../config/api.config';
import { DisponibilidadSalida } from '../models/disponibilidad-salida.model';
import { RespuestaGeneral } from '../models/respuesta-general.model';

/** Consulta CQRS de opciones seleccionables antes de reservar. */
@Injectable({providedIn:'root'})
export class DisponibilidadSalidaService {
  private readonly http=inject(HttpClient); private readonly api=inject(API_V2);
  consultar(salidaId:string):Observable<RespuestaGeneral<DisponibilidadSalida>> { return this.http.get<RespuestaGeneral<DisponibilidadSalida>>(`${this.api.consultas}/public/experiencias/salidas/${salidaId}/disponibilidad`); }
}
