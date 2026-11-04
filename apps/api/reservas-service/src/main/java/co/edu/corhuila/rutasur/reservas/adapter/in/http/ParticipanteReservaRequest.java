package co.edu.corhuila.rutasur.reservas.adapter.in.http;

import jakarta.validation.constraints.*;

/**
 * Datos validados de un viajero incluido en una reserva.
 */
public record ParticipanteReservaRequest(@NotBlank String nombres, @NotBlank String apellidos,
                                         @NotBlank String tipoDocumento, @NotBlank String numeroDocumento) {
}
