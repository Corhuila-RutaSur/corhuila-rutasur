import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
/** Modal reutilizable de RutaSur para acciones que requieren confirmación explícita. */
@Component({ selector: 'rs-modal', templateUrl: './modal.component.html', styleUrl: './modal.component.scss', changeDetection: ChangeDetectionStrategy.OnPush })
export class ModalComponent { @Input() abierto = false; @Input({ required: true }) titulo = ''; @Input() descripcion = ''; @Input() textoConfirmar = 'Confirmar'; @Input() variante: 'peligro' | 'principal' = 'principal'; @Output() confirmar = new EventEmitter<void>(); @Output() cancelar = new EventEmitter<void>(); }
