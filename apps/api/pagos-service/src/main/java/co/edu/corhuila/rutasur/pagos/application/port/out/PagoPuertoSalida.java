package co.edu.corhuila.rutasur.pagos.application.port.out;

import co.edu.corhuila.rutasur.pagos.domain.model.Pago;

import java.util.*;

/**
 * Puerto de persistencia de pagos idempotentes.
 */
public interface PagoPuertoSalida {
    Optional<Pago> buscarPorCorrelacion(UUID c);

    Optional<Pago> buscarAprobadoPorReserva(UUID reservaIdExterna);

    Pago guardar(Pago p);
}
