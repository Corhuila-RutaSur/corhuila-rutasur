package co.edu.corhuila.rutasur.reservas.adapter.in.http;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Vista HTTP segura de una salida programada.
 */
public record SalidaResponse(UUID id, UUID experienciaIdExterna, String experienciaNombre,
                             OffsetDateTime fechaHoraInicio, int cuposTotales, int cuposDisponibles, String estado) {
}
