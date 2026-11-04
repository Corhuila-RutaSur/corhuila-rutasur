import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_V2 } from '../config/api.config';
import { ProgramacionServicio, ProgramacionServicioSolicitud, SalidaCreacionSolicitud, SalidaExperiencia, ServicioExperiencia } from '../models/planificacion.model';
import { RespuestaGeneral } from '../models/respuesta-general.model';

/** Orquesta los comandos V2 de Catálogo y Reservas para configurar una experiencia. */
@Injectable({ providedIn: 'root' })
export class PlanificacionService {
  private readonly http = inject(HttpClient);
  private readonly api = inject(API_V2);

  /** Lista servicios comerciales activos desde el dueño Catálogo. */
  servicios(experienciaId: string): Observable<RespuestaGeneral<ServicioExperiencia[]>> {
    return this.http.get<RespuestaGeneral<ServicioExperiencia[]>>(`${this.api.catalogo}/administracion/experiencias/${experienciaId}/servicios`);
  }

  /** Crea un servicio en el catálogo de una experiencia. */
  crearServicio(experienciaId: string, datos: Pick<ServicioExperiencia, 'tipo' | 'nombre' | 'descripcion'>): Observable<RespuestaGeneral<ServicioExperiencia>> {
    return this.http.post<RespuestaGeneral<ServicioExperiencia>>(`${this.api.catalogo}/administracion/experiencias/${experienciaId}/servicios`, datos);
  }

  /** Consulta las salidas administradas por Reservas; la pantalla filtra su experiencia. */
  salidas(): Observable<RespuestaGeneral<SalidaExperiencia[]>> {
    return this.http.get<RespuestaGeneral<SalidaExperiencia[]>>(`${this.api.reservas}/administracion/salidas`);
  }

  /** Registra la fecha y capacidad de una salida en Reservas. */
  crearSalida(datos: SalidaCreacionSolicitud): Observable<RespuestaGeneral<SalidaExperiencia>> {
    return this.http.post<RespuestaGeneral<SalidaExperiencia>>(`${this.api.reservas}/administracion/salidas`, datos);
  }

  /** Habilita un servicio de Catálogo para la salida con precio y cupos propios. */
  crearProgramacion(salidaId: string, datos: ProgramacionServicioSolicitud): Observable<RespuestaGeneral<ProgramacionServicio>> {
    return this.http.post<RespuestaGeneral<ProgramacionServicio>>(`${this.api.reservas}/administracion/salidas/${salidaId}/servicios`, datos);
  }
}
