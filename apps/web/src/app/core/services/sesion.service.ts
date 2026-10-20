import { Injectable, signal } from '@angular/core';
import { TokensSesion } from '../models/autenticacion.model';
const LLAVE_SESION = 'rutasur.sesion';
@Injectable({ providedIn: 'root' }) export class SesionService {
  private readonly estadoInterno = signal<TokensSesion | null>(this.leer()); readonly sesion = this.estadoInterno.asReadonly();
  guardar(tokens: TokensSesion): void { sessionStorage.setItem(LLAVE_SESION, JSON.stringify(tokens)); this.estadoInterno.set(tokens); }
  cerrar(): void { sessionStorage.removeItem(LLAVE_SESION); this.estadoInterno.set(null); }
  accessToken(): string | null { return this.estadoInterno()?.accessToken ?? null; }
  refreshToken(): string | null { return this.estadoInterno()?.refreshToken ?? null; }
  tieneRol(rol: string): boolean { return this.estadoInterno()?.roles.includes(rol) ?? false; }
  usuarioId(): number | null { try { const token = this.accessToken(); if (!token) return null; const carga = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/'); return Number(JSON.parse(atob(carga)).sub) || null; } catch { return null; } }
  private leer(): TokensSesion | null { const valor = sessionStorage.getItem(LLAVE_SESION); if (!valor) return null; try { return JSON.parse(valor) as TokensSesion; } catch { this.cerrar(); return null; } }
}
