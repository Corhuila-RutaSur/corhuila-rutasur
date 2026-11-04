package co.edu.corhuila.rutasur.notificaciones.application.port.in;

import co.edu.corhuila.rutasur.notificaciones.domain.model.Notificacion;

import java.util.List;
import java.util.UUID;

/**
 * Consulta el buzón interno de un usuario.
 */
public interface ConsultarNotificacionesUseCase {
    List<Notificacion> consultar(UUID usuarioId);
}
