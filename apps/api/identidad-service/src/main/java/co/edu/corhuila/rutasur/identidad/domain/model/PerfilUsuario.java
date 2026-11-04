package co.edu.corhuila.rutasur.identidad.domain.model;

import java.util.List;
import java.util.UUID;

/**
 * Vista segura del perfil de identidad que puede exponerse al usuario autenticado.
 */
public record PerfilUsuario(UUID id, String tipoDocumento, String numeroDocumento, String nombres, String apellidos,
                            String correo, String telefono, boolean activo, List<RolUsuario> roles) {
}
