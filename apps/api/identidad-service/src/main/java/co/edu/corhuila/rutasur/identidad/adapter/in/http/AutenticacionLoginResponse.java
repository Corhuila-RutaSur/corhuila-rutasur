package co.edu.corhuila.rutasur.identidad.adapter.in.http;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Datos entregados al cliente después de un inicio de sesión exitoso.
 */
public record AutenticacionLoginResponse(UUID usuarioId, String correo, List<String> roles, String accessToken,
                                         String refreshToken, OffsetDateTime accessExpiraEn,
                                         OffsetDateTime refreshExpiraEn) {
}
