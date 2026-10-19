import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { MatDialogModule } from '@angular/material/dialog';
import { BotonComponent } from '../boton/boton.component';

/** Contenido reutilizable para dialogos de confirmacion. */
@Component({
  selector: 'rs-modal',
  imports: [MatDialogModule, BotonComponent],
  templateUrl: './modal.component.html',
  styleUrl: './modal.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ModalComponent {
  @Input({ required: true }) titulo = '';
  @Input() textoConfirmar = 'Confirmar';
  @Output() confirmar = new EventEmitter<void>();
  @Output() cancelar = new EventEmitter<void>();
}
