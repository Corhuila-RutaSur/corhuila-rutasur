package co.edu.corhuila.rutasur.identidad.application.port.out;

import co.edu.corhuila.rutasur.identidad.domain.model.RolUsuario;

import java.util.List;

/**
 * Puerto de lectura del catálogo real de roles activos.
 */
public interface RolPuertoSalida {
    List<RolUsuario> listarActivos();

    /**
     * Indica si todos los roles solicitados existen y permanecen habilitados.
     */
    boolean todosActivos(List<RolUsuario> roles);
}
