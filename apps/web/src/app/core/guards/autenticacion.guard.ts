import { inject } from '@angular/core'; import { CanActivateFn, Router } from '@angular/router'; import { SesionService } from '../services/sesion.service';
export const autenticacionGuard: CanActivateFn = (_, estado) => { const sesion = inject(SesionService); return sesion.accessToken() ? true : inject(Router).createUrlTree(['/iniciar-sesion'], { queryParams: { retorno: estado.url } }); };
