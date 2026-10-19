package co.edu.corhuila.rutasur.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * Implementa la comprobacion del formato de telefonos moviles colombianos.
 */
public class ValidadorTelefonoColombiano implements ConstraintValidator<TelefonoColombiano, String> {

    private static final Pattern PATRON_TELEFONO_COLOMBIANO = Pattern.compile("^3\\d{9}$");

    /**
     * Comprueba que el telefono tenga diez digitos y comience en tres.
     *
     * @param telefono valor recibido en la solicitud.
     * @param contexto contexto de Bean Validation.
     * @return {@code true} si el valor es nulo o cumple el patron; {@code false} en caso contrario.
     */
    @Override
    public boolean isValid(String telefono, ConstraintValidatorContext contexto) {
        return telefono == null || PATRON_TELEFONO_COLOMBIANO.matcher(telefono).matches();
    }
}
