import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

export type TipoBanner = 'informacion' | 'exito' | 'advertencia' | 'error';

/** Mensaje destacado reutilizable para estados y orientaciones en pantalla. */
@Component({
  selector: 'rs-banner',
  templateUrl: './banner.component.html',
  styleUrl: './banner.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BannerComponent {
  @Input() tipo: TipoBanner = 'informacion';
  @Input({ required: true }) mensaje = '';
}
