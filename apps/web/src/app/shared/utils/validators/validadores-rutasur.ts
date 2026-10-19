import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

/**
 * Validador para confirmar que dos controles contienen el mismo valor.
 */
export function valoresCoinciden(nombrePrimerControl: string, nombreSegundoControl: string): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const primerControl = control.get(nombrePrimerControl);
    const segundoControl = control.get(nombreSegundoControl);

    if (!primerControl || !segundoControl || !segundoControl.value) {
      return null;
    }

    return primerControl.value === segundoControl.value ? null : { valoresNoCoinciden: true };
  };
}
