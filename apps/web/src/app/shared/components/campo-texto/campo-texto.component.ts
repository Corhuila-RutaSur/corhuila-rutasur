import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { obtenerMensajeError } from '../../utils/validators/mensajes-validacion';

/** Campo de texto reutilizable que muestra su mensaje de validacion bajo el input. */
@Component({
  selector: 'rs-campo-texto',
  imports: [ReactiveFormsModule, MatFormFieldModule, MatInputModule],
  templateUrl: './campo-texto.component.html',
  styleUrl: './campo-texto.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CampoTextoComponent {
  @Input({ required: true }) etiqueta = '';
  @Input({ required: true }) control!: FormControl<string | null>;
  @Input() tipo: 'text' | 'email' | 'password' | 'number' = 'text';
  @Input() ayuda = '';
  @Input() marcador = '';

  /** Expone un unico mensaje de error consistente para el campo. */
  obtenerError(): string | null {
    return obtenerMensajeError(this.control);
  }
}
