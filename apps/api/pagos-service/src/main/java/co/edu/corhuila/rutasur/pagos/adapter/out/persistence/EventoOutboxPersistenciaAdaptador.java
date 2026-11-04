package co.edu.corhuila.rutasur.pagos.adapter.out.persistence;

import co.edu.corhuila.rutasur.pagos.application.port.out.*;

import java.time.*;
import java.util.*;

import lombok.*;
import org.springframework.stereotype.Component;

/**
 * Adaptador que persiste eventos de pago junto al pago aprobado.
 */
@Component
@RequiredArgsConstructor
public class EventoOutboxPersistenciaAdaptador implements EventoOutboxPuertoSalida {
    private final EventoOutboxRepositorioJpa repo;

    @Override
    public void registrar(String t, UUID a, UUID c, String d) {
        EventoOutboxEntidad e = new EventoOutboxEntidad();
        e.setId(UUID.randomUUID());
        e.setTipo(t);
        e.setVersion(1);
        e.setAgregadoId(a);
        e.setCorrelacionId(c);
        e.setDatos(d);
        e.setOcurridoEn(OffsetDateTime.now());
        repo.save(e);
    }
}
