import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { URL_API } from '../config/api.config';
import { ProgramacionServicio, SalidaExperiencia, ServicioExperiencia } from '../models/planificacion.model';
import { RespuestaGeneral } from '../models/respuesta-general.model';

/** Centraliza el acceso HTTP a servicios, salidas y precios de las experiencias. */
@Injectable({ providedIn: 'root' })
export class PlanificacionService {
  private readonly http = inject(HttpClient);
  private readonly url = inject(URL_API);

  /** Obtiene los servicios activos que un visitante puede consultar. */
  serviciosPublicos(id: number): Observable<RespuestaGeneral<ServicioExperiencia[]>> {
    return this.http.get<RespuestaGeneral<ServicioExperiencia[]>>(`${this.url}/public/experiencias/${id}/servicios`);
  }

  /** Obtiene las salidas abiertas que se pueden reservar. */
  salidasPublicas(id: number): Observable<RespuestaGeneral<SalidaExperiencia[]>> {
    return this.http.get<RespuestaGeneral<SalidaExperiencia[]>>(`${this.url}/public/experiencias/${id}/salidas`);
  }

  salidaPublica(id: number): Observable<RespuestaGeneral<SalidaExperiencia>> { return this.http.get<RespuestaGeneral<SalidaExperiencia>>(`${this.url}/public/salidas/${id}`); }

  /** Obtiene los servicios con precio de una salida pública concreta. */
  programacionesPublicas(id: number): Observable<RespuestaGeneral<ProgramacionServicio[]>> {
    return this.http.get<RespuestaGeneral<ProgramacionServicio[]>>(`${this.url}/public/salidas/${id}/programaciones`);
  }

  /** Obtiene los servicios para la configuración administrativa. */
  servicios(id: number): Observable<RespuestaGeneral<ServicioExperiencia[]>> {
    return this.serviciosPublicos(id);
  }

  /** Crea un servicio dentro de una experiencia. */
  crearServicio(id: number, datos: Partial<ServicioExperiencia>): Observable<RespuestaGeneral<ServicioExperiencia>> {
    return this.http.post<RespuestaGeneral<ServicioExperiencia>>(`${this.url}/experiencias/${id}/servicios`, datos);
  }

  /** Obtiene todas las salidas de una experiencia para administración. */
  salidas(id: number): Observable<RespuestaGeneral<SalidaExperiencia[]>> {
    return this.http.get<RespuestaGeneral<SalidaExperiencia[]>>(`${this.url}/experiencias/${id}/salidas`);
  }

  /** Crea una salida administrativa. */
  crearSalida(id: number, datos: Partial<SalidaExperiencia>): Observable<RespuestaGeneral<SalidaExperiencia>> {
    return this.http.post<RespuestaGeneral<SalidaExperiencia>>(`${this.url}/experiencias/${id}/salidas`, datos);
  }

  /** Obtiene precios y cupos de servicios para una salida administrativa. */
  programaciones(id: number): Observable<RespuestaGeneral<ProgramacionServicio[]>> {
    return this.http.get<RespuestaGeneral<ProgramacionServicio[]>>(`${this.url}/salidas/${id}/programaciones`);
  }

  /** Programa un servicio dentro de una salida. */
  crearProgramacion(id: number, datos: Partial<ProgramacionServicio>): Observable<RespuestaGeneral<ProgramacionServicio>> {
    return this.http.post<RespuestaGeneral<ProgramacionServicio>>(`${this.url}/salidas/${id}/programaciones`, datos);
  }
}
