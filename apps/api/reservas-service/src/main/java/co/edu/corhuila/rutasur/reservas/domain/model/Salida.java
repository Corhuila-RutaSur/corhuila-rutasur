package co.edu.corhuila.rutasur.reservas.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Salida programada y dueña local de cupos disponibles para reservar.
 */
public record Salida(UUID id, UUID experienciaIdExterna, String experienciaNombre, OffsetDateTime fechaHoraInicio,
                     int cuposTotales, int cuposDisponibles, String estado, OffsetDateTime creadoEn) {
}
