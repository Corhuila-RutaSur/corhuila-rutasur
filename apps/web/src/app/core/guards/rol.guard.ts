import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { SesionService } from '../services/sesion.service';

/** Protege rutas cuya funcionalidad pertenece a un rol concreto. */
export const rolGuard = (rolRequerido: string): CanActivateFn => () => {
  const sesion = inject(SesionService);
  return sesion.tieneRol(rolRequerido)
    ? true
    : inject(Router).createUrlTree(['/']);
};
