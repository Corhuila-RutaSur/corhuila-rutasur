package co.edu.corhuila.rutasur.reservas.adapter.in.http;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Solicitud administrativa para programar una salida.
 */
public record SalidaRequest(@NotNull(message = "salida.experiencia.obligatoria") UUID experienciaIdExterna,
                            @NotBlank(message = "salida.experiencia.nombre.obligatorio") String experienciaNombre,
                            @NotNull(message = "salida.fecha.obligatoria") @Future(message = "salida.fecha.invalida") OffsetDateTime fechaHoraInicio,
                            @Min(value = 1, message = "salida.cupos.invalidos") int cuposTotales) {
}
