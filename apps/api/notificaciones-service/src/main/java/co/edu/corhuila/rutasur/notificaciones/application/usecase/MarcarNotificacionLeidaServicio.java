package co.edu.corhuila.rutasur.notificaciones.application.usecase;

import co.edu.corhuila.rutasur.notificaciones.application.port.in.MarcarNotificacionLeidaUseCase;
import co.edu.corhuila.rutasur.notificaciones.application.port.out.NotificacionPuertoSalida;
import co.edu.corhuila.rutasur.notificaciones.domain.model.Notificacion;

import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Marca un aviso propio como leído.
 */
@Service
@RequiredArgsConstructor
public class MarcarNotificacionLeidaServicio implements MarcarNotificacionLeidaUseCase {
    private final NotificacionPuertoSalida notificaciones;

    @Override
    @Transactional
    public Notificacion marcarLeida(UUID usuarioId, UUID notificacionId) {
        Notificacion actual = notificaciones.buscarPorIdYUsuario(notificacionId, usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("No existe la notificación solicitada para este usuario."));
        if ("LEIDA".equals(actual.estado())) return actual;
        return notificaciones.guardar(new Notificacion(actual.id(), actual.eventoId(), actual.usuarioIdExterno(), actual.tipo(),
                actual.titulo(), actual.mensaje(), "LEIDA", actual.creadaEn(), OffsetDateTime.now()));
    }
}
