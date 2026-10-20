import { Injectable, inject } from '@angular/core';
import { URL_API } from '../config/api.config';

/** Convierte rutas relativas de archivos del backend en URLs públicas consumibles por Angular. */
@Injectable({ providedIn: 'root' })
export class ArchivoPublicoService {
  private readonly urlServidor = inject(URL_API).replace(/\/api\/v1$/, '');
  resolver(ruta: string | undefined, respaldo: string): string {
    if (!ruta) return respaldo;
    return ruta.startsWith('http') ? ruta : `${this.urlServidor}${ruta}`;
  }
}
