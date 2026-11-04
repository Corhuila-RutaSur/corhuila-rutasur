package co.edu.corhuila.rutasur.reservas.application.port.in;

/**
 * Identidad mínima de un viajero incluido en una reserva.
 */
public record ParticipanteReservaCommand(String nombres, String apellidos, String tipoDocumento,
                                         String numeroDocumento) {
}
