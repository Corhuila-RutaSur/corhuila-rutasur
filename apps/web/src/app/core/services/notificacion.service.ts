import { Injectable, inject } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ToastrComponent } from '../../shared/components/toastr/toastr.component';

export type TipoNotificacion = 'exito' | 'informacion' | 'advertencia' | 'error';

/**
 * Centraliza las notificaciones breves de la aplicacion.
 */
@Injectable({ providedIn: 'root' })
export class NotificacionService {
  private readonly barraNotificacion = inject(MatSnackBar);

  /** Muestra un mensaje temporal con una apariencia coherente. */
  mostrar(mensaje: string, tipo: TipoNotificacion = 'informacion'): void {
    this.barraNotificacion.openFromComponent(ToastrComponent, {
      duration: tipo === 'error' ? 6000 : 4000,
      data: { mensaje, tipo },
      panelClass: ['rs-contenedor-toastr'],
      horizontalPosition: 'end',
      verticalPosition: 'top',
    });
  }
}
