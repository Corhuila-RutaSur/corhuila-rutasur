package co.edu.corhuila.rutasur.notificaciones.application.usecase;

import co.edu.corhuila.rutasur.notificaciones.application.port.in.RegistrarNotificacionCommand;
import co.edu.corhuila.rutasur.notificaciones.application.port.in.RegistrarNotificacionUseCase;
import co.edu.corhuila.rutasur.notificaciones.application.port.out.EventoInboxPuertoSalida;
import co.edu.corhuila.rutasur.notificaciones.application.port.out.NotificacionPuertoSalida;
import co.edu.corhuila.rutasur.notificaciones.domain.model.Notificacion;

import java.time.OffsetDateTime;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Guarda un aviso interno e Inbox en la misma transacción.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RegistrarNotificacionServicio implements RegistrarNotificacionUseCase {
    private final NotificacionPuertoSalida notificaciones;
    private final EventoInboxPuertoSalida inbox;

    @Override
    @Transactional
    public void registrar(RegistrarNotificacionCommand comando) {
        if (inbox.yaProcesado(comando.eventoId())) {
            log.info("servicio=notificaciones evento={} resultado=duplicado", comando.tipoEvento());
            return;
        }
        notificaciones.guardar(new Notificacion(null, comando.eventoId(), comando.usuarioId(), comando.tipoEvento(),
                comando.titulo(), comando.mensaje(), "NO_LEIDA", OffsetDateTime.now(), null));
        inbox.registrarProcesado(comando.eventoId(), comando.tipoEvento(), comando.origen());
        log.info("servicio=notificaciones evento={} usuarioId={} resultado=notificacion-creada", comando.tipoEvento(), comando.usuarioId());
    }
}
