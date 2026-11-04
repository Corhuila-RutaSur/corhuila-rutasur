package co.edu.corhuila.rutasur.identidad.application.port.in;

import java.util.UUID;

/**
 * Caso de uso de eliminación lógica administrativa de una identidad.
 */
public interface InactivarUsuarioUseCase {
    void inactivar(UUID usuarioId, UUID correlacionId);
}
