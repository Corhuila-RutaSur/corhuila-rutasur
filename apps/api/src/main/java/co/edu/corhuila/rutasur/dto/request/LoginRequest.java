package co.edu.corhuila.rutasur.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Credenciales para iniciar sesión.
 */
public record LoginRequest(@NotBlank @Email String correo, @NotBlank String contrasenia) {
}
