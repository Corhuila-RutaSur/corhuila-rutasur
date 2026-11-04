import { InjectionToken } from '@angular/core';

/** Direcciones directas de V2 para desarrollo local, mientras Gateway permanece fuera de alcance. */
export interface ApiV2Config { identidad: string; catalogo: string; reservas: string; pagos: string; consultas: string; }
export const API_V2 = new InjectionToken<ApiV2Config>('API_V2', { factory: (): ApiV2Config => ({ identidad: 'http://localhost:8091/api/v2', catalogo: 'http://localhost:8092/api/v2', reservas: 'http://localhost:8093/api/v2', pagos: 'http://localhost:8094/api/v2', consultas: 'http://localhost:8097/api/v2' }) });
export const URL_CATALOGO_ARCHIVOS = 'http://localhost:8092';
/** Compatibilidad temporal: los módulos administrativos pendientes aún consumen esta URL hasta su migración V2. */
export const URL_API = new InjectionToken<string>('URL_API_TRANSITORIA', { factory: () => 'http://localhost:8090/api/v1' });
