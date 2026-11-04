package co.edu.corhuila.rutasur.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Valida un numero movil colombiano: diez digitos que inician en tres.
 */
@Documented
@Constraint(validatedBy = ValidadorTelefonoColombiano.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface TelefonoColombiano {

    /**
     * Obtiene la clave internacionalizable del mensaje de validacion.
     *
     * @return clave de mensaje para el telefono invalido.
     */
    String message() default "{usuario.telefono.invalido}";

    /**
     * Obtiene los grupos de validacion.
     *
     * @return grupos de validacion.
     */
    Class<?>[] groups() default {};

    /**
     * Obtiene la carga adicional de la restriccion.
     *
     * @return cargas de validacion.
     */
    Class<? extends Payload>[] payload() default {};
}
