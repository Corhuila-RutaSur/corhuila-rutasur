package co.edu.corhuila.rutasur.pagos.adapter.out.persistence;

import co.edu.corhuila.rutasur.pagos.application.port.out.*;
import co.edu.corhuila.rutasur.pagos.domain.model.*;

import java.time.*;
import java.util.*;

import lombok.*;
import org.springframework.stereotype.Component;

/**
 * Adaptador JPA idempotente de pagos.
 */
@Component
@RequiredArgsConstructor
public class PagoPersistenciaAdaptador implements PagoPuertoSalida {
    private final PagoRepositorioJpa repo;

    private Pago map(PagoEntidad e) {
        return new Pago(e.getId(), e.getReservaIdExterna(), e.getCorrelacionId(), e.getMonto(), e.getMoneda(), e.getEstado(), e.getReferenciaProveedor());
    }

    @Override
    public Optional<Pago> buscarPorCorrelacion(UUID c) {
        return repo.findByCorrelacionId(c).map(this::map);
    }

    @Override
    public Optional<Pago> buscarAprobadoPorReserva(UUID reservaIdExterna) {
        return repo.findFirstByReservaIdExternaAndEstadoOrderByCreadoEnDesc(reservaIdExterna, "APROBADO").map(this::map);
    }

    @Override
    public Pago guardar(Pago p) {
        PagoEntidad e = new PagoEntidad();
        e.setId(UUID.randomUUID());
        e.setReservaIdExterna(p.reservaIdExterna());
        e.setCorrelacionId(p.correlacionId());
        e.setMonto(p.monto());
        e.setMoneda(p.moneda());
        e.setEstado(p.estado());
        e.setReferenciaProveedor(p.referenciaProveedor());
        e.setCreadoEn(OffsetDateTime.now());
        return map(repo.save(e));
    }
}
