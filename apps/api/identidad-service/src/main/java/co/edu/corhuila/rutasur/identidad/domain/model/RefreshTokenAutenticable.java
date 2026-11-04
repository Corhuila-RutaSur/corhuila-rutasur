package co.edu.corhuila.rutasur.identidad.domain.model;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Refresh token bloqueado para una rotación segura dentro de la transacción local.
 */
public record RefreshTokenAutenticable(UUID id, UUID sesionId, UUID usuarioId, String correo, boolean usuarioActivo,
                                       boolean sesionActiva, boolean usado, OffsetDateTime expiraEn,
                                       List<RolUsuario> roles) {
}
