package co.edu.corhuila.rutasur.pagos.application.port.out;

import java.util.UUID;

/**
 * Puerto que registra hechos de pago junto a la transacción local.
 */
public interface EventoOutboxPuertoSalida {
    void registrar(String tipo, UUID agregadoId, UUID correlacionId, String datos);
}
