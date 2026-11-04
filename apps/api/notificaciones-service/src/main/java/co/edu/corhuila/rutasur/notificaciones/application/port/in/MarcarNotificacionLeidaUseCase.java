package co.edu.corhuila.rutasur.notificaciones.application.port.in;

import co.edu.corhuila.rutasur.notificaciones.domain.model.Notificacion;

import java.util.UUID;

/**
 * Marca como leída una notificación perteneciente al usuario autenticado.
 */
public interface MarcarNotificacionLeidaUseCase {
    Notificacion marcarLeida(UUID usuarioId, UUID notificacionId);
}
