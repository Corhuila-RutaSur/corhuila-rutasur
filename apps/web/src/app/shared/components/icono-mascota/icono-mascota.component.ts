import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

/** Representacion temporal de iconos o mascota hasta contar con recursos oficiales. */
@Component({
  selector: 'rs-icono-mascota',
  templateUrl: './icono-mascota.component.html',
  styleUrl: './icono-mascota.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class IconoMascotaComponent {
  @Input() simbolo = '🧭';
  @Input() descripcion = 'RutaSur';
  @Input() tamano: 'pequeno' | 'mediano' | 'grande' = 'mediano';
}
