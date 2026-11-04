package co.edu.corhuila.rutasur.reservas.adapter.in.http;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Solicitud para habilitar un servicio de Catálogo en una salida.
 */
public record ProgramacionServicioRequest(@NotNull UUID servicioIdExterno, @NotBlank String nombre,
                                          @NotNull @DecimalMin("0.0") BigDecimal precio,
                                          @Min(1) int cuposTotales) {
}
