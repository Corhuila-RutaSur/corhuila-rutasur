package co.edu.corhuila.rutasur.identidad.application.port.in;

import co.edu.corhuila.rutasur.identidad.domain.model.RolUsuario;

import java.util.List;
import java.util.UUID;

/**
 * Datos para crear una identidad con roles elegidos por un administrador autorizado.
 */
public record CrearUsuarioAdministrativoCommand(String tipoDocumento, String numeroDocumento, String nombres,
                                                String apellidos, String correo, String telefono, String contrasenia,
                                                List<RolUsuario> roles, UUID correlacionId) {
}
