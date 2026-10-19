package co.edu.corhuila.rutasur.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;

/** Datos necesarios para crear una reserva configurable. */
public record ReservaCreacionRequest(
        @NotNull @Positive Long usuarioId,
        @NotNull @Positive Long salidaExperienciaId,
        @NotEmpty List<@NotNull @Positive Long> programacionesServicioIds,
        @NotEmpty List<@Valid ParticipanteReservaRequest> participantes,
        @Size(max = 1000) String observaciones) { }
