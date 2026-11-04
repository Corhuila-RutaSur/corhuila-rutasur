package co.edu.corhuila.rutasur.reservas.adapter.in.http;

import jakarta.validation.constraints.Size;

/**
 * Motivo opcional informado por el dueño al cancelar una reserva.
 */
public record CancelacionReservaRequest(
        @Size(max = 255, message = "El motivo no puede superar 255 caracteres.") String motivo) {
}
