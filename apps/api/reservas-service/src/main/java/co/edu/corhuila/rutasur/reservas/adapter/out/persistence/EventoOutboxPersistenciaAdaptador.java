package co.edu.corhuila.rutasur.reservas.adapter.out.persistence;

import co.edu.corhuila.rutasur.reservas.application.port.out.EventoOutboxPuertoSalida;

import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Implementación JPA del patrón Outbox en Reservas.
 */
@Component
@RequiredArgsConstructor
public class EventoOutboxPersistenciaAdaptador implements EventoOutboxPuertoSalida {
    private final EventoOutboxRepositorioJpa repositorio;

    @Override
    public void registrar(String tipo, UUID agregadoId, UUID correlacionId, String datos) {
        EventoOutboxEntidad evento = new EventoOutboxEntidad();
        evento.setId(UUID.randomUUID());
        evento.setTipo(tipo);
        evento.setVersion(1);
        evento.setAgregadoId(agregadoId);
        evento.setCorrelacionId(correlacionId);
        evento.setDatos(datos);
        evento.setOcurridoEn(OffsetDateTime.now());
        repositorio.save(evento);
    }
}
