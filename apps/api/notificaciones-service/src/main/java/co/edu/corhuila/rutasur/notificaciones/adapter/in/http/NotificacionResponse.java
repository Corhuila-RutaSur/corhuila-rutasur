package co.edu.corhuila.rutasur.notificaciones.adapter.in.http;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Representación HTTP de una notificación interna.
 */
public record NotificacionResponse(UUID id, String tipo, String titulo, String mensaje, String estado,
                                   OffsetDateTime creadaEn, OffsetDateTime leidaEn) {
}
