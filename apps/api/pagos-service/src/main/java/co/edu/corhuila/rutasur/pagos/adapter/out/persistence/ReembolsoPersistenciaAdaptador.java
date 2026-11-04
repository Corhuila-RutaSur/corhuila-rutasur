package co.edu.corhuila.rutasur.pagos.adapter.out.persistence;

import co.edu.corhuila.rutasur.pagos.application.port.out.ReembolsoPuertoSalida;
import co.edu.corhuila.rutasur.pagos.domain.model.Reembolso;

import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Adaptador JPA de reembolsos idempotentes por pago aprobado.
 */
@Component
@RequiredArgsConstructor
public class ReembolsoPersistenciaAdaptador implements ReembolsoPuertoSalida {
    private final ReembolsoRepositorioJpa repositorio;

    @Override
    public boolean existePorPagoId(UUID pagoId) {
        return repositorio.existsByPagoId(pagoId);
    }

    @Override
    public Reembolso guardar(Reembolso reembolso) {
        ReembolsoEntidad entidad = new ReembolsoEntidad();
        entidad.setId(UUID.randomUUID());
        entidad.setPagoId(reembolso.pagoId());
        entidad.setMonto(reembolso.monto());
        entidad.setEstado(reembolso.estado());
        entidad.setMotivo(reembolso.motivo());
        entidad.setCreadoEn(OffsetDateTime.now());
        ReembolsoEntidad guardado = repositorio.save(entidad);
        return new Reembolso(guardado.getId(), guardado.getPagoId(), guardado.getMonto(), guardado.getEstado(), guardado.getMotivo());
    }
}
