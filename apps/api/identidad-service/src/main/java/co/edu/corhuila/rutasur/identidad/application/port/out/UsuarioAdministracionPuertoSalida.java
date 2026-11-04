package co.edu.corhuila.rutasur.identidad.application.port.out;

import co.edu.corhuila.rutasur.identidad.domain.model.UsuarioAdministrable;
import co.edu.corhuila.rutasur.identidad.domain.model.RolUsuario;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de consultas administrativas de identidades, sin entidades JPA expuestas.
 */
public interface UsuarioAdministracionPuertoSalida {
    List<UsuarioAdministrable> listar();

    Optional<UsuarioAdministrable> buscarPorId(UUID usuarioId);

    /**
     * Actualiza los datos de contacto permitidos de una identidad existente.
     */
    void actualizar(UUID usuarioId, String nombres, String apellidos, String correo, String telefono);

    /**
     * Sustituye de forma explícita los roles asignados a una identidad.
     */
    void reemplazarRoles(UUID usuarioId, List<RolUsuario> roles);

    /**
     * Marca una identidad como inactiva sin eliminar su evidencia persistida.
     */
    void inactivar(UUID usuarioId);
}
