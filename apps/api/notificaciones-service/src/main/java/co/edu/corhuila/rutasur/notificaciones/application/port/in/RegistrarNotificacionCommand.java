package co.edu.corhuila.rutasur.notificaciones.application.port.in;

import java.util.UUID;

/**
 * Datos normalizados de un evento que produce una notificación interna.
 */
public record RegistrarNotificacionCommand(UUID eventoId, String tipoEvento, String origen, UUID usuarioId,
                                           String titulo, String mensaje) {
}
