package co.edu.corhuila.rutasur.identidad.application.port.out;

import java.util.UUID;

/**
 * Puerto para registrar hechos de identidad en Outbox.
 */
public interface EventoIdentidadPuertoSalida {
    void registrarUsuarioCreado(UUID usuarioId, UUID correlacionId);
}
