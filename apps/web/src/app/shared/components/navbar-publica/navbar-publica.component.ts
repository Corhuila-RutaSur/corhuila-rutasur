import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AutenticacionService } from '../../../core/services/autenticacion.service';
import { SesionService } from '../../../core/services/sesion.service';
@Component({selector:'rs-navbar-publica',imports:[RouterLink],templateUrl:'./navbar-publica.component.html',styleUrl:'./navbar-publica.component.scss',changeDetection:ChangeDetectionStrategy.OnPush}) export class NavbarPublicaComponent { readonly sesion=inject(SesionService); private readonly autenticacion=inject(AutenticacionService); private readonly router=inject(Router); cerrarSesion():void{this.autenticacion.cerrarSesion().subscribe({complete:()=>this.router.navigateByUrl('/')});} }
