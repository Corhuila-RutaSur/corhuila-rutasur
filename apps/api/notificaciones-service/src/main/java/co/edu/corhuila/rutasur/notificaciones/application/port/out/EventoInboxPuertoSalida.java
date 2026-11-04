package co.edu.corhuila.rutasur.notificaciones.application.port.out;

import java.util.UUID;

/**
 * Registro de eventos recibidos para procesamiento idempotente.
 */
public interface EventoInboxPuertoSalida {
    boolean yaProcesado(UUID eventoId);

    void registrarProcesado(UUID eventoId, String tipo, String origen);
}
