package co.edu.corhuila.rutasur.reservas.adapter.in.http;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/** Contrato de lectura para el historial de reservas de un usuario. */
public record ReservaUsuarioResponse(UUID id, String codigo, UUID salidaId, UUID experienciaId, String experienciaNombre,
                                    OffsetDateTime fechaHoraInicio, int cantidadParticipantes, BigDecimal total,
                                    String estado, OffsetDateTime creadaEn) {
}
