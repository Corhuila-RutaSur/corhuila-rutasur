package co.edu.corhuila.rutasur.reservas.application.port.in;

import java.util.UUID;

/**
 * Solicitud explícita de cancelación por el dueño de la reserva.
 */
public record CancelarReservaCommand(UUID reservaId, UUID usuarioIdExterno, String motivo, UUID correlacionId) {
}
