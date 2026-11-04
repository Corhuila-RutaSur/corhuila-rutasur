package co.edu.corhuila.rutasur.identidad.application.port.in;

import co.edu.corhuila.rutasur.identidad.domain.model.UsuarioAdministrable;

import java.util.List;
import java.util.UUID;

/**
 * Casos de consulta de usuarios disponibles para administración.
 */
public interface ConsultarUsuariosAdministracionUseCase {
    List<UsuarioAdministrable> listar();

    UsuarioAdministrable consultar(UUID usuarioId);
}
