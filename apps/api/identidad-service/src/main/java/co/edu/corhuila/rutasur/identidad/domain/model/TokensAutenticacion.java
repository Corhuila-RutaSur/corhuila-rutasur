package co.edu.corhuila.rutasur.identidad.domain.model;

import java.time.OffsetDateTime;

/**
 * Tokens emitidos por una autenticación exitosa, sin exponer sus hashes internos.
 */
public record TokensAutenticacion(String accessToken, String refreshToken, OffsetDateTime accessExpiraEn,
                                  OffsetDateTime refreshExpiraEn) {
}
