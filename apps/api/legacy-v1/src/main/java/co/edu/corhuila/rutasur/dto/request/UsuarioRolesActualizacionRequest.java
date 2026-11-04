package co.edu.corhuila.rutasur.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

/**
 * Roles que un administrador asigna a una cuenta activa.
 */
public record UsuarioRolesActualizacionRequest(@NotEmpty List<@NotBlank String> roles) {
}
