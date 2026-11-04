package co.edu.corhuila.rutasur.reservas.application.port.out;

import java.util.UUID;

/**
 * Evita procesar dos veces un mismo evento externo.
 */
public interface EventoInboxPuertoSalida {
    boolean yaProcesado(UUID eventoId);

    void registrarProcesado(UUID eventoId, String tipo, String origen);
}
