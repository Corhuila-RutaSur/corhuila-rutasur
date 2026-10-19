import { Injectable, inject } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';

export type TipoNotificacion = 'exito' | 'informacion' | 'advertencia' | 'error';

/**
 * Centraliza las notificaciones breves de la aplicacion.
 */
@Injectable({ providedIn: 'root' })
export class NotificacionService {
  private readonly barraNotificacion = inject(MatSnackBar);

  /** Muestra un mensaje temporal con una apariencia coherente. */
  mostrar(mensaje: string, tipo: TipoNotificacion = 'informacion'): void {
    this.barraNotificacion.open(mensaje, 'Cerrar', {
      duration: tipo === 'error' ? 6000 : 4000,
      panelClass: [`notificacion-${tipo}`],
      horizontalPosition: 'end',
      verticalPosition: 'top',
    });
  }
}
