import { HttpClient } from '@angular/common/http'; import { Injectable, inject } from '@angular/core'; import { Observable, finalize, tap } from 'rxjs'; import { API_V2 } from '../config/api.config'; import { CredencialesInicioSesion, RegistroUsuario, TokensSesion } from '../models/autenticacion.model'; import { RespuestaGeneral } from '../models/respuesta-general.model'; import { SesionService } from './sesion.service';
@Injectable({ providedIn: 'root' }) export class AutenticacionService {
  private readonly http = inject(HttpClient); private readonly api = inject(API_V2); private readonly sesion = inject(SesionService);
  iniciarSesion(credenciales: CredencialesInicioSesion): Observable<RespuestaGeneral<TokensSesion>> { return this.http.post<RespuestaGeneral<TokensSesion>>(`${this.api.identidad}/public/autenticacion/login`, credenciales).pipe(tap(r => this.guardar(r))); }
  registrar(datos: RegistroUsuario): Observable<RespuestaGeneral<unknown>> { return this.http.post<RespuestaGeneral<unknown>>(`${this.api.identidad}/public/usuarios`, datos); }
  renovar(): Observable<RespuestaGeneral<TokensSesion>> { return this.http.post<RespuestaGeneral<TokensSesion>>(`${this.api.identidad}/public/autenticacion/refresh`, { refreshToken: this.sesion.refreshToken() }).pipe(tap(r => this.guardar(r))); }
  cerrarSesion(): Observable<RespuestaGeneral<void>> { return this.http.post<RespuestaGeneral<void>>(`${this.api.identidad}/autenticacion/logout`, {}).pipe(finalize(() => this.sesion.cerrar())); }
  cerrarSesionLocal(): void { this.sesion.cerrar(); }
  private guardar(respuesta: RespuestaGeneral<TokensSesion>): void { if (respuesta.data) this.sesion.guardar(respuesta.data); }
}
