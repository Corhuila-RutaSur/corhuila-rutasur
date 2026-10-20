import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MAT_SNACK_BAR_DATA, MatSnackBarRef } from '@angular/material/snack-bar';
import { TipoNotificacion } from '../../../core/services/notificacion.service';
export interface DatosToastr { mensaje: string; tipo: TipoNotificacion; }
@Component({selector:'rs-toastr',imports:[MatIconModule],templateUrl:'./toastr.component.html',styleUrl:'./toastr.component.scss',changeDetection:ChangeDetectionStrategy.OnPush})
export class ToastrComponent { readonly datos=inject<DatosToastr>(MAT_SNACK_BAR_DATA); private referencia=inject(MatSnackBarRef<ToastrComponent>); readonly iconos:Record<TipoNotificacion,string>={exito:'check_circle',error:'error',informacion:'info',advertencia:'warning'}; cerrar():void{this.referencia.dismiss();} }
