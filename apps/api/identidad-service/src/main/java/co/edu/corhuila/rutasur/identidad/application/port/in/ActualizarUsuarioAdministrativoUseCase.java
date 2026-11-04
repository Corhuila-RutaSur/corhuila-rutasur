package co.edu.corhuila.rutasur.identidad.application.port.in;

import co.edu.corhuila.rutasur.identidad.domain.model.UsuarioAdministrable;

/**
 * Caso de uso de edición administrativa de datos y roles.
 */
public interface ActualizarUsuarioAdministrativoUseCase {
    UsuarioAdministrable actualizar(ActualizarUsuarioAdministrativoCommand comando);
}
