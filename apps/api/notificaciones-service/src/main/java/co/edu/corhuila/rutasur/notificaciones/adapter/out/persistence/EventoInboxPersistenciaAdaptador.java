package co.edu.corhuila.rutasur.notificaciones.adapter.out.persistence;

import co.edu.corhuila.rutasur.notificaciones.application.port.out.EventoInboxPuertoSalida;

import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Adaptador de idempotencia Inbox.
 */
@Component
@RequiredArgsConstructor
public class EventoInboxPersistenciaAdaptador implements EventoInboxPuertoSalida {
    private final EventoInboxRepositorioJpa repositorio;

    @Override
    public boolean yaProcesado(UUID eventoId) {
        return repositorio.existsById(eventoId);
    }

    @Override
    public void registrarProcesado(UUID eventoId, String tipo, String origen) {
        EventoInboxEntidad evento = new EventoInboxEntidad();
        evento.setIdEvento(eventoId);
        evento.setTipo(tipo);
        evento.setOrigen(origen);
        evento.setRecibidoEn(OffsetDateTime.now());
        evento.setProcesadoEn(OffsetDateTime.now());
        evento.setEstado("PROCESADO");
        repositorio.save(evento);
    }
}
