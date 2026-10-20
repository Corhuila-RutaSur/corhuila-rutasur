import { InjectionToken } from '@angular/core';
export const URL_API = new InjectionToken<string>('URL_API', { factory: () => 'http://localhost:8090/api/v1' });
