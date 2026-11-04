package co.edu.corhuila.rutasur.pagos.application.port.out;

import co.edu.corhuila.rutasur.pagos.domain.model.Reembolso;

import java.util.UUID;

/**
 * Puerto de persistencia de reembolsos, independiente del agregado Reserva.
 */
public interface ReembolsoPuertoSalida {
    boolean existePorPagoId(UUID pagoId);

    Reembolso guardar(Reembolso reembolso);
}
