package co.edu.corhuila.rutasur.identidad.application.port.in;

import co.edu.corhuila.rutasur.identidad.domain.model.Usuario;

/**
 * Caso de uso exclusivo para creación de usuarios por administración.
 */
public interface CrearUsuarioAdministrativoUseCase {
    Usuario crear(CrearUsuarioAdministrativoCommand comando);
}
