package co.edu.corhuila.rutasur.pagos.application.port.out;

import java.util.UUID;

/**
 * Puerto Inbox para evitar reembolsar dos veces el mismo evento externo.
 */
public interface EventoInboxPuertoSalida {
    boolean yaProcesado(UUID eventoId);

    void registrarProcesado(UUID eventoId, String tipo, String origen);
}
