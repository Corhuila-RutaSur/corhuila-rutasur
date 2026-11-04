package co.edu.corhuila.rutasur.identidad.adapter.out.security;

import co.edu.corhuila.rutasur.identidad.application.port.out.TokenAutenticacionPuertoSalida;
import co.edu.corhuila.rutasur.identidad.domain.exception.ExcepcionAutenticacion;
import co.edu.corhuila.rutasur.identidad.domain.model.RolUsuario;
import co.edu.corhuila.rutasur.identidad.domain.model.TokenAccesoValidado;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import javax.crypto.SecretKey;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Firma access tokens JWT y produce refresh tokens aleatorios no reutilizables.
 */
@Component
@RequiredArgsConstructor
public class JwtTokenAutenticacionAdaptador implements TokenAutenticacionPuertoSalida {
    private final JwtPropiedades propiedades;

    /**
     * Genera un JWT firmado con el usuario, la sesión y las autoridades asignadas.
     */
    @Override
    public String generarAccessToken(UUID usuarioId, UUID sesionId, List<RolUsuario> roles, OffsetDateTime expiraEn) {
        return Jwts.builder()
                .subject(usuarioId.toString())
                .id(UUID.randomUUID().toString())
                .claim("sid", sesionId.toString())
                .claim("roles", roles.stream().map(Enum::name).toList())
                .issuedAt(java.util.Date.from(OffsetDateTime.now().toInstant()))
                .expiration(java.util.Date.from(expiraEn.toInstant()))
                .signWith(clave())
                .compact();
    }

    /**
     * Genera un refresh token de alta entropía sin contenido interpretable.
     */
    @Override
    public String generarRefreshToken() {
        byte[] aleatorio = new byte[48];
        new java.security.SecureRandom().nextBytes(aleatorio);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(aleatorio);
    }

    /**
     * Calcula SHA-256 para persistir exclusivamente el hash del refresh token.
     */
    @Override
    public String calcularHashRefresh(String refreshToken) {
        try {
            return Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-256")
                    .digest(refreshToken.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException excepcion) {
            throw new IllegalStateException("SHA-256 no está disponible en la JVM.", excepcion);
        }
    }

    /**
     * Verifica firma y claims mínimos, distinguiendo expiración de token malformado.
     */
    @Override
    public TokenAccesoValidado validarAccessToken(String accessToken) {
        try {
            Claims claims = Jwts.parser().verifyWith(clave()).build().parseSignedClaims(accessToken).getPayload();
            @SuppressWarnings("unchecked")
            List<String> nombresRoles = claims.get("roles", List.class);
            List<RolUsuario> roles = nombresRoles == null ? List.of() : nombresRoles.stream().map(RolUsuario::valueOf).toList();
            return new TokenAccesoValidado(UUID.fromString(claims.getSubject()), UUID.fromString(claims.get("sid", String.class)),
                    claims.getId(), OffsetDateTime.ofInstant(claims.getExpiration().toInstant(), java.time.ZoneOffset.UTC), roles);
        } catch (ExpiredJwtException excepcion) {
            throw new ExcepcionAutenticacion("autenticacion.token.expirado");
        } catch (JwtException | IllegalArgumentException excepcion) {
            throw new ExcepcionAutenticacion("autenticacion.token.invalido");
        }
    }

    private SecretKey clave() {
        return Keys.hmacShaKeyFor(propiedades.secreto().getBytes(StandardCharsets.UTF_8));
    }
}
