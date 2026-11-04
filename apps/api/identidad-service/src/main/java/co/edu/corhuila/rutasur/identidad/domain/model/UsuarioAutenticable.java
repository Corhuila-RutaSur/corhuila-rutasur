package co.edu.corhuila.rutasur.identidad.domain.model;

import java.util.List;
import java.util.UUID;

/**
 * Identidad mínima necesaria para autenticar y autorizar un usuario activo.
 */
public record UsuarioAutenticable(UUID id, String correo, String passwordHash, boolean activo, List<RolUsuario> roles) {
}
