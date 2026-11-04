package co.edu.corhuila.rutasur.notificaciones.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Notificación interna visible para un usuario dentro de RutaSur.
 */
public record Notificacion(UUID id, UUID eventoId, UUID usuarioIdExterno, String tipo, String titulo,
                           String mensaje, String estado, OffsetDateTime creadaEn, OffsetDateTime leidaEn) {
}
