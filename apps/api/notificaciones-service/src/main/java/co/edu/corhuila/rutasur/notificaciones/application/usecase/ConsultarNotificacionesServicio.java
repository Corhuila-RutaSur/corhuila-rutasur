package co.edu.corhuila.rutasur.notificaciones.application.usecase;

import co.edu.corhuila.rutasur.notificaciones.application.port.in.ConsultarNotificacionesUseCase;
import co.edu.corhuila.rutasur.notificaciones.application.port.out.NotificacionPuertoSalida;
import co.edu.corhuila.rutasur.notificaciones.domain.model.Notificacion;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Consulta el historial de avisos internos de un usuario.
 */
@Service
@RequiredArgsConstructor
public class ConsultarNotificacionesServicio implements ConsultarNotificacionesUseCase {
    private final NotificacionPuertoSalida notificaciones;

    @Override
    public List<Notificacion> consultar(UUID usuarioId) {
        return notificaciones.consultarPorUsuario(usuarioId);
    }
}
