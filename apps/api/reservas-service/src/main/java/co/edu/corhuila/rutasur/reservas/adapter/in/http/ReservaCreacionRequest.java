package co.edu.corhuila.rutasur.reservas.adapter.in.http;

import jakarta.validation.*;
import jakarta.validation.constraints.*;

import java.util.*;

/**
 * Solicitud de reserva de una salida con selección común para sus viajeros.
 */
public record ReservaCreacionRequest(@NotNull UUID usuarioIdExterno, @NotNull UUID salidaId,
                                     @Min(1) int cantidadParticipantes,
                                     @NotEmpty List<@Valid ParticipanteReservaRequest> participantes,
                                     List<UUID> programacionesServicio, List<UUID> programacionesActividad) {
}
