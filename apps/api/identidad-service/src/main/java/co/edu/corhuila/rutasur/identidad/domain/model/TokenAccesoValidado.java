package co.edu.corhuila.rutasur.identidad.domain.model;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Claims verificadas criptográficamente de un access token JWT.
 */
public record TokenAccesoValidado(UUID usuarioId, UUID sesionId, String jti, OffsetDateTime expiraEn,
                                  List<RolUsuario> roles) {
}
