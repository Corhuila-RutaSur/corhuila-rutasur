package co.edu.corhuila.rutasur.reservas.adapter.in.http;

import java.math.*;
import java.util.*;

/**
 * Respuesta segura de una reserva creada.
 */
public record ReservaResponse(UUID id, String codigo, UUID salidaId, int cantidadParticipantes, BigDecimal total,
                              String estado) {
}
