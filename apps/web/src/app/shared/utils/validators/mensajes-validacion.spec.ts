import { FormControl, Validators } from '@angular/forms';
import { obtenerMensajeError } from './mensajes-validacion';

describe('obtenerMensajeError', () => {
  it('muestra el mensaje obligatorio luego de interactuar con el campo', () => {
    const control = new FormControl('', { nonNullable: true, validators: [Validators.required] });
    control.markAsTouched();

    expect(obtenerMensajeError(control)).toBe('Este campo es obligatorio.');
  });

  it('no muestra errores antes de que el usuario interactue', () => {
    const control = new FormControl('', { nonNullable: true, validators: [Validators.required] });

    expect(obtenerMensajeError(control)).toBeNull();
  });
});
