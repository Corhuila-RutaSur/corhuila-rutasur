package co.edu.corhuila.rutasur.identidad.application.port.out;

import co.edu.corhuila.rutasur.identidad.domain.model.RolUsuario;
import co.edu.corhuila.rutasur.identidad.domain.model.TokenAccesoValidado;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Puerto criptográfico que emite los tokens de autenticación.
 */
public interface TokenAutenticacionPuertoSalida {

    /**
     * Firma un access token con identidad, sesión y roles.
     */
    String generarAccessToken(UUID usuarioId, UUID sesionId, List<RolUsuario> roles, OffsetDateTime expiraEn);

    /**
     * Genera un valor aleatorio de refresh token para almacenar solo como hash.
     */
    String generarRefreshToken();

    /**
     * Calcula un hash no reversible del refresh token recibido.
     */
    String calcularHashRefresh(String refreshToken);

    /**
     * Verifica firma, estructura y vencimiento de un access token JWT.
     */
    TokenAccesoValidado validarAccessToken(String accessToken);
}
