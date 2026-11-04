package co.edu.corhuila.rutasur.identidad.application.port.in;

import co.edu.corhuila.rutasur.identidad.domain.model.RolUsuario;

import java.util.List;

/**
 * Caso de uso de consulta de roles disponibles para administración.
 */
public interface ListarRolesUseCase {
    List<RolUsuario> listar();
}
