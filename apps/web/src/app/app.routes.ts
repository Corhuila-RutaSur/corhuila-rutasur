import { Routes } from '@angular/router';
import { IniciarSesionComponent } from './features/autenticacion/pages/iniciar-sesion/iniciar-sesion.component';
import { RegistroComponent } from './features/autenticacion/pages/registro/registro.component';
import { CatalogoComponent } from './features/experiencias/pages/catalogo/catalogo.component';
import { DetalleExperienciaComponent } from './features/experiencias/pages/detalle-experiencia/detalle-experiencia.component';
import { PanelAdministrativoComponent } from './features/administracion/pages/panel-administrativo/panel-administrativo.component';
import { autenticacionGuard } from './core/guards/autenticacion.guard';
import { rolGuard } from './core/guards/rol.guard';
import { GestionExperienciasComponent } from './features/administracion/pages/gestion-experiencias/gestion-experiencias.component';
import { GestionUsuariosComponent } from './features/administracion/pages/gestion-usuarios/gestion-usuarios.component';
import { ConfiguracionExperienciaComponent } from './features/administracion/pages/configuracion-experiencia/configuracion-experiencia.component';
import { MisReservasComponent } from './features/reservas/pages/mis-reservas/mis-reservas.component';

export const routes: Routes = [
  { path: '', component: CatalogoComponent, title: 'RutaSur | Explora el sur' },
  { path: 'iniciar-sesion', component: IniciarSesionComponent, title: 'RutaSur | Iniciar sesión' },
  { path: 'registro', component: RegistroComponent, title: 'RutaSur | Registro' },
  { path: 'experiencias', component: CatalogoComponent, title: 'RutaSur | Experiencias' },
  { path: 'experiencias/:id', component: DetalleExperienciaComponent, title: 'RutaSur | Experiencia' },
  { path: 'administracion', component: PanelAdministrativoComponent, canActivate: [autenticacionGuard, rolGuard('ADMINISTRADOR')], title: 'RutaSur | Administración' },
  { path: 'administracion/experiencias', component: GestionExperienciasComponent, canActivate: [autenticacionGuard, rolGuard('ADMINISTRADOR')], title: 'RutaSur | Gestión de experiencias' },
  { path: 'administracion/experiencias/:id/configuracion', component: ConfiguracionExperienciaComponent, canActivate: [autenticacionGuard, rolGuard('ADMINISTRADOR')], title: 'RutaSur | Configurar experiencia' },
  { path: 'administracion/usuarios', component: GestionUsuariosComponent, canActivate: [autenticacionGuard, rolGuard('ADMINISTRADOR')], title: 'RutaSur | Gestión de usuarios' },
  { path: 'mis-reservas', component: MisReservasComponent, canActivate: [autenticacionGuard], title: 'RutaSur | Mis reservas' },
  { path: '**', redirectTo: '' },
];
