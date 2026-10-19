import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';

export type AparienciaBoton = 'primario' | 'secundario' | 'texto' | 'peligro';

/** Boton reutilizable con variantes visuales controladas por RutaSur. */
@Component({
  selector: 'rs-boton',
  templateUrl: './boton.component.html',
  styleUrl: './boton.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BotonComponent {
  @Input({ required: true }) texto = '';
  @Input() apariencia: AparienciaBoton = 'primario';
  @Input() tipo: 'button' | 'submit' | 'reset' = 'button';
  @Input() deshabilitado = false;
  @Output() accion = new EventEmitter<void>();

  /** Emite la accion solicitada solo cuando el boton esta habilitado. */
  ejecutarAccion(): void {
    if (!this.deshabilitado) {
      this.accion.emit();
    }
  }
}
