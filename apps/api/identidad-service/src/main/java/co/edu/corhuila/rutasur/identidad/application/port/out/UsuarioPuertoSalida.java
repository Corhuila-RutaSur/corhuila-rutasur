package co.edu.corhuila.rutasur.identidad.application.port.out;

import co.edu.corhuila.rutasur.identidad.domain.model.RolUsuario;
import co.edu.corhuila.rutasur.identidad.domain.model.Usuario;

import java.util.UUID;

/**
 * Puerto de persistencia de usuarios y asignaciones de rol.
 */
public interface UsuarioPuertoSalida {
    boolean existeNumeroDocumento(String numeroDocumento);

    boolean existeCorreo(String correo);

    boolean existeTelefono(String telefono);

    Usuario guardar(Usuario usuario);

    void asignarRol(UUID usuarioId, RolUsuario rol);
}
