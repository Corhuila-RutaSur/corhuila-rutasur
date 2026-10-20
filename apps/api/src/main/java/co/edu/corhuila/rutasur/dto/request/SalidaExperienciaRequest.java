package co.edu.corhuila.rutasur.dto.request;

import co.edu.corhuila.rutasur.entity.EstadoSalida;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.OffsetDateTime;

/** Datos administrativos para programar una salida de experiencia. */
public record SalidaExperienciaRequest(
        @NotNull OffsetDateTime fechaHoraInicio,
        OffsetDateTime fechaHoraFin,
        @NotNull @Positive Integer cuposTotales,
        @NotNull EstadoSalida estado,
        String observaciones) { }
