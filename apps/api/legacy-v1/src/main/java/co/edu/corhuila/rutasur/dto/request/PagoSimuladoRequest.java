package co.edu.corhuila.rutasur.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Datos no sensibles de un pago simulado para una reserva pendiente.
 */
public record PagoSimuladoRequest(@NotBlank @Size(max = 50) String metodoPago) {
}
