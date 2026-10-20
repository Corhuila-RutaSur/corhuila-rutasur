import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http'; import { inject } from '@angular/core'; import { Router } from '@angular/router'; import { catchError, switchMap, throwError } from 'rxjs'; import { RespuestaGeneral } from '../../models/respuesta-general.model'; import { AutenticacionService } from '../../services/autenticacion.service'; import { SesionService } from '../../services/sesion.service';
/** Adjunta Bearer y rota una vez los tokens ante access token vencido. */
export const autenticacionInterceptor: HttpInterceptorFn = (solicitud, siguiente) => {
  const sesion = inject(SesionService); const autenticacion = inject(AutenticacionService); const router = inject(Router);
  const esPublica = solicitud.url.includes('/api/v1/public/') || solicitud.url.includes('/autenticacion/login') || solicitud.url.includes('/autenticacion/refresh');
  const conToken = !esPublica && sesion.accessToken() ? solicitud.clone({ setHeaders: { Authorization: `Bearer ${sesion.accessToken()}` } }) : solicitud;
  return siguiente(conToken).pipe(catchError((error: HttpErrorResponse) => {
    const codigo = (error.error as RespuestaGeneral<unknown> | undefined)?.codigo;
    if (esPublica || codigo !== 'autenticacion.token.expirado' || !sesion.refreshToken()) return throwError(() => error);
    return autenticacion.renovar().pipe(switchMap(() => siguiente(solicitud.clone({ setHeaders: { Authorization: `Bearer ${sesion.accessToken()}` } }))), catchError(errorRenovacion => { autenticacion.cerrarSesionLocal(); router.navigate(['/iniciar-sesion']); return throwError(() => errorRenovacion); }));
  }));
};
