package co.edu.corhuila.rutasur.reservas.application.port.out;

import java.util.UUID;

/**
 * Registra hechos que deben publicarse tras confirmar la transacción local.
 */
public interface EventoOutboxPuertoSalida {
    void registrar(String tipo, UUID agregadoId, UUID correlacionId, String datos);
}
