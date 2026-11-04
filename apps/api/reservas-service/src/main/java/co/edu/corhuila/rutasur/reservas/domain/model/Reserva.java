package co.edu.corhuila.rutasur.reservas.domain.model;

import java.math.*;
import java.time.*;
import java.util.*;

/**
 * Reserva local que compromete cupos de una salida y conserva su total comercial.
 */
public record Reserva(UUID id, String codigo, UUID usuarioIdExterno, UUID salidaId, int cantidadParticipantes,
                      BigDecimal total, String estado, UUID correlacionId, OffsetDateTime creadaEn) {
}
