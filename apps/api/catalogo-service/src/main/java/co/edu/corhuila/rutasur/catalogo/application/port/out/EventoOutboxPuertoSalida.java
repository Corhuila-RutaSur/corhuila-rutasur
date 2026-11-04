package co.edu.corhuila.rutasur.catalogo.application.port.out;

import java.util.UUID;

/**
 * Registra hechos de Catálogo junto con la transacción comercial.
 */
public interface EventoOutboxPuertoSalida {
    void registrar(String tipo, UUID agregadoId, UUID correlacionId, String datos);
}
