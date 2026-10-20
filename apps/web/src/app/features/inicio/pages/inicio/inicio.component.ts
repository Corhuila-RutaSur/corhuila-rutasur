import { ChangeDetectionStrategy, Component } from '@angular/core'; import { RouterLink } from '@angular/router';
@Component({ selector: 'rs-inicio', imports: [RouterLink], templateUrl: './inicio.component.html', styleUrl: './inicio.component.scss', changeDetection: ChangeDetectionStrategy.OnPush }) export class InicioComponent {}
