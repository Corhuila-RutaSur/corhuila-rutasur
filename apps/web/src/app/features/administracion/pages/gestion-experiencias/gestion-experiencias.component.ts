import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { Experiencia, ExperienciaSolicitud, TipoExperiencia } from '../../../../core/models/experiencia.model';
import { ArchivoPublicoService } from '../../../../core/services/archivo-publico.service';
import { ExperienciaService } from '../../../../core/services/experiencia.service';
import { NotificacionService } from '../../../../core/services/notificacion.service';

/** Administración V2 de experiencias e imágenes, operada directamente contra Catálogo. */
@Component({ selector: 'rs-gestion-experiencias', imports: [FormsModule, RouterLink], templateUrl: './gestion-experiencias.component.html', styleUrl: './gestion-experiencias.component.scss', changeDetection: ChangeDetectionStrategy.OnPush })
export class GestionExperienciasComponent {
  private readonly servicio = inject(ExperienciaService);
  private readonly archivosPublicos = inject(ArchivoPublicoService);
  private readonly aviso = inject(NotificacionService);
  readonly experiencias = signal<Experiencia[]>([]);
  readonly tipos = signal<TipoExperiencia[]>([]);
  readonly guardando = signal(false);
  archivos: File[] = [];
  formulario: ExperienciaSolicitud = this.nuevoFormulario();

  constructor() { this.cargar(); this.cargarTipos(); }

  seleccionarArchivos(evento: Event): void { this.archivos = Array.from((evento.target as HTMLInputElement).files ?? []); }
  urlImagen(ruta: string | undefined): string { return this.archivosPublicos.resolver(ruta, '/assets/mascota/tati-bienvenida.png'); }

  cargar(): void {
    this.servicio.listarAdministracion().subscribe({ next: respuesta => this.experiencias.set(respuesta.data ?? []), error: error => this.aviso.mostrar(error.error?.mensaje ?? 'No fue posible consultar las experiencias.', 'error') });
  }

  cargarTipos(): void {
    this.servicio.listarTiposActivos().subscribe({ next: respuesta => this.tipos.set(respuesta.data ?? []), error: error => this.aviso.mostrar(error.error?.mensaje ?? 'No fue posible consultar los tipos de experiencia.', 'error') });
  }

  /** Crea la experiencia y, tras tener su UUID, adjunta los archivos seleccionados. */
  crear(): void {
    if (!this.formulario.tipoExperienciaId.trim() || !this.formulario.nombre.trim() || !this.formulario.descripcion.trim() || !this.formulario.destino.trim() || !this.formulario.duracionHoras) {
      this.aviso.mostrar('Completa tipo, nombre, destino, duración y descripción.', 'advertencia'); return;
    }
    this.guardando.set(true);
    this.servicio.crear({ ...this.formulario, recomendaciones: this.formulario.recomendaciones?.trim() || undefined }).subscribe({
      next: respuesta => {
        const experienciaId = respuesta.data?.id;
        if (!experienciaId || !this.archivos.length) { this.finalizarCreacion(); return; }
        forkJoin(this.archivos.map((archivo, indice) => this.servicio.adjuntarImagen(experienciaId, archivo, indice === 0, indice + 1))).subscribe({
          next: () => this.finalizarCreacion(),
          error: error => { this.guardando.set(false); this.cargar(); this.aviso.mostrar(error.error?.mensaje ?? 'La experiencia fue creada, pero no se cargaron todas las imágenes.', 'advertencia'); }
        });
      },
      error: error => { this.guardando.set(false); this.aviso.mostrar(error.error?.mensaje ?? 'No fue posible crear la experiencia.', 'error'); }
    });
  }

  inactivar(id: string): void {
    if (!confirm('¿Deseas inactivar esta experiencia?')) return;
    this.servicio.inactivar(id).subscribe({ next: respuesta => { this.aviso.mostrar(respuesta.mensaje || 'Experiencia inactivada.', 'exito'); this.cargar(); }, error: error => this.aviso.mostrar(error.error?.mensaje ?? 'No fue posible inactivar la experiencia.', 'error') });
  }

  private finalizarCreacion(): void {
    this.aviso.mostrar('Experiencia e imágenes guardadas.', 'exito'); this.formulario = this.nuevoFormulario(); this.archivos = []; this.guardando.set(false); this.cargar();
  }
  private nuevoFormulario(): ExperienciaSolicitud { return { tipoExperienciaId: '', nombre: '', descripcion: '', destino: '', duracionHoras: 1, recomendaciones: '' }; }
}
