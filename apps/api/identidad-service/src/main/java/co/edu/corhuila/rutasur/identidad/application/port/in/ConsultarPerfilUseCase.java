package co.edu.corhuila.rutasur.identidad.application.port.in;

import co.edu.corhuila.rutasur.identidad.domain.model.PerfilUsuario;

import java.util.UUID;

/**
 * Caso de uso de consulta del perfil propio autenticado.
 */
public interface ConsultarPerfilUseCase {

    /**
     * Devuelve el perfil seguro de la identidad autenticada.
     */
    PerfilUsuario consultar(UUID usuarioId);
}
