package co.edu.corhuila.rutasur.identidad.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Sesión local que controla la vigencia de tokens emitidos para un usuario.
 */
public record SesionAutenticacion(UUID id, UUID usuarioId, OffsetDateTime expiraEn) {
}
