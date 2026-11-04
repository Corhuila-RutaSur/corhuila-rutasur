package co.edu.corhuila.rutasur.notificaciones.application.port.out;

import co.edu.corhuila.rutasur.notificaciones.domain.model.Notificacion;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de persistencia de notificaciones internas.
 */
public interface NotificacionPuertoSalida {
    Notificacion guardar(Notificacion notificacion);

    List<Notificacion> consultarPorUsuario(UUID usuarioId);

    Optional<Notificacion> buscarPorIdYUsuario(UUID notificacionId, UUID usuarioId);
}
