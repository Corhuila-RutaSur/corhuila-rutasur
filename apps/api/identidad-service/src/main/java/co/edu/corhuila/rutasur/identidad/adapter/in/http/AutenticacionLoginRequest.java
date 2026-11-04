package co.edu.corhuila.rutasur.identidad.adapter.in.http;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Credenciales requeridas para iniciar una sesión de RutaSur.
 */
public record AutenticacionLoginRequest(
        @NotBlank(message = "autenticacion.correo.obligatorio") @Email(message = "autenticacion.correo.formato") String correo,
        @NotBlank(message = "autenticacion.contrasenia.obligatoria") @Size(max = 100, message = "autenticacion.contrasenia.longitud") String contrasenia) {
}
