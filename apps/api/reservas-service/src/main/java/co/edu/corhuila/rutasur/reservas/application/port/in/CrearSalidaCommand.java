package co.edu.corhuila.rutasur.reservas.application.port.in;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Datos validados para programar una salida de una experiencia conocida.
 */
public record CrearSalidaCommand(UUID experienciaIdExterna, String experienciaNombre, OffsetDateTime fechaHoraInicio,
                                 int cuposTotales, UUID correlacionId) {
}
