import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { MatCardModule } from '@angular/material/card';

/** Contenedor visual reutilizable para agrupar informacion relacionada. */
@Component({
  selector: 'rs-panel',
  imports: [MatCardModule],
  templateUrl: './panel.component.html',
  styleUrl: './panel.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PanelComponent {
  @Input() titulo = '';
}
