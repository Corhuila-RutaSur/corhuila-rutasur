package co.edu.corhuila.rutasur.reservas.application.usecase;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/** Vista de reserva compuesta localmente para la sección Mis reservas. */
public record ReservaUsuarioDetalle(UUID id, String codigo, UUID salidaId, UUID experienciaId, String experienciaNombre,
                                   OffsetDateTime fechaHoraInicio, int cantidadParticipantes, BigDecimal total,
                                   String estado, OffsetDateTime creadaEn) {
}
