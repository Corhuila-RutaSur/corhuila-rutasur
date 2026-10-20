import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AutenticacionService } from '../../../../core/services/autenticacion.service';

/** Punto de entrada para la operación administrativa de RutaSur. */
@Component({
  selector: 'rs-panel-administrativo',
  imports: [RouterLink],
  templateUrl: './panel-administrativo.component.html',
  styleUrl: './panel-administrativo.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PanelAdministrativoComponent {
  private readonly autenticacion = inject(AutenticacionService);
  private readonly router = inject(Router);

  cerrarSesion(): void {
    this.autenticacion.cerrarSesionLocal();
    this.router.navigateByUrl('/iniciar-sesion');
  }
}
