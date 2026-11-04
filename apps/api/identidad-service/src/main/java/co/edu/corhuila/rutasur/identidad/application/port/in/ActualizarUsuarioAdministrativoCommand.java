package co.edu.corhuila.rutasur.identidad.application.port.in;

import co.edu.corhuila.rutasur.identidad.domain.model.RolUsuario;

import java.util.List;
import java.util.UUID;

/**
 * Datos editables y roles nuevos para una identidad administrada.
 */
public record ActualizarUsuarioAdministrativoCommand(UUID usuarioId, String nombres, String apellidos, String correo,
                                                     String telefono, List<RolUsuario> roles, UUID correlacionId) {
}
