package co.edu.corhuila.rutasur.pagos.application.port.in;

import java.util.UUID;

/**
 * Hecho de cancelación que puede requerir una devolución financiera.
 */
public record ProcesarReembolsoCommand(UUID eventoId, UUID reservaIdExterna, String motivo, UUID correlacionId) {
}
