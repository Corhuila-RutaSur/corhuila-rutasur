package co.edu.corhuila.rutasur.catalogo.adapter.out.persistence;

import co.edu.corhuila.rutasur.catalogo.application.port.out.EventoOutboxPuertoSalida;

import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Persistencia transaccional del Outbox de Catálogo.
 */
@Component
@RequiredArgsConstructor
public class EventoOutboxPersistenciaAdaptador implements EventoOutboxPuertoSalida {
    private final EventoOutboxRepositorioJpa repositorio;

    @Override
    public void registrar(String tipo, UUID agregadoId, UUID correlacionId, String datos) {
        EventoOutboxEntidad e = new EventoOutboxEntidad();
        e.setId(UUID.randomUUID());
        e.setTipo(tipo);
        e.setVersion(1);
        e.setAgregadoId(agregadoId);
        e.setCorrelacionId(correlacionId);
        e.setDatos(datos);
        e.setOcurridoEn(OffsetDateTime.now());
        repositorio.save(e);
    }
}
