import { AbstractControl, ValidationErrors } from '@angular/forms';

/**
 * Mensajes funcionales centralizados para los errores de formularios.
 */
export const MENSAJES_VALIDACION: Record<string, (error: unknown) => string> = {
  required: () => 'Este campo es obligatorio.',
  email: () => 'Ingresa un correo electronico valido.',
  minlength: (error) => {
    const longitud = (error as { requiredLength: number }).requiredLength;
    return `Ingresa al menos ${longitud} caracteres.`;
  },
  maxlength: (error) => {
    const longitud = (error as { requiredLength: number }).requiredLength;
    return `Ingresa maximo ${longitud} caracteres.`;
  },
  pattern: () => 'El formato ingresado no es valido.',
  min: (error) => `El valor minimo permitido es ${(error as { min: number }).min}.`,
  max: (error) => `El valor maximo permitido es ${(error as { max: number }).max}.`,
  valoresNoCoinciden: () => 'Los valores ingresados no coinciden.',
};

/**
 * Obtiene el primer mensaje visible de error para un control invalido.
 */
export function obtenerMensajeError(control: AbstractControl | null): string | null {
  if (!control || !control.invalid || !(control.touched || control.dirty)) {
    return null;
  }

  const errores: ValidationErrors | null = control.errors;
  if (!errores) {
    return null;
  }

  const codigoError = Object.keys(errores)[0];
  const creadorMensaje = MENSAJES_VALIDACION[codigoError];

  return creadorMensaje ? creadorMensaje(errores[codigoError]) : 'El valor ingresado no es valido.';
}
