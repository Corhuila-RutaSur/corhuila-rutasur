package co.edu.corhuila.rutasur.identidad.domain.model;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Proyección segura que usa un administrador para gestionar identidades.
 */
public record UsuarioAdministrable(UUID id, String tipoDocumento, String numeroDocumento, String nombres,
                                   String apellidos, String correo, String telefono, boolean activo,
                                   OffsetDateTime creadoEn, List<RolUsuario> roles) {
}
