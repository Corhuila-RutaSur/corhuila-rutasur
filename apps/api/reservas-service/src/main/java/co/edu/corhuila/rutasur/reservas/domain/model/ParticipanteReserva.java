package co.edu.corhuila.rutasur.reservas.domain.model;

import java.util.UUID;

/**
 * Viajero individual incluido en una reserva.
 */
public record ParticipanteReserva(UUID id, UUID reservaId, String nombres, String apellidos, String tipoDocumento,
                                  String numeroDocumento) {
}
