package co.edu.corhuila.rutasur.identidad.application.port.out;

import co.edu.corhuila.rutasur.identidad.domain.model.PerfilUsuario;

import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de lectura de perfiles, independiente de entidades de persistencia.
 */
public interface PerfilUsuarioPuertoSalida {

    /**
     * Busca el perfil y roles de una identidad por su identificador externo.
     */
    Optional<PerfilUsuario> buscarPerfil(UUID usuarioId);
}
