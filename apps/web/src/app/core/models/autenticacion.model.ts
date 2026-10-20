export interface CredencialesInicioSesion { correo: string; contrasenia: string; }
export interface TokensSesion { accessToken: string; refreshToken: string; tipoToken: string; expiraEnSegundos: number; roles: string[]; }
export interface RegistroUsuario { tipoDocumento: string; numeroDocumento: string; nombres: string; apellidos: string; correo: string; telefono: string; contrasenia: string; }
