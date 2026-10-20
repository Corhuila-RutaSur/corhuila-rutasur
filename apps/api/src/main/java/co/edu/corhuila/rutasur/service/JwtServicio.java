package co.edu.corhuila.rutasur.service;
import co.edu.corhuila.rutasur.config.security.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import io.jsonwebtoken.io.DecodingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/** Firma y valida access tokens de RutaSur. */
@Service
@RequiredArgsConstructor
public class JwtServicio {
    private static final Logger LOG = LoggerFactory.getLogger(JwtServicio.class);
    private final JwtProperties propiedades;
    public String generar(Long usuarioId, UUID sesionId, List<String> roles, UUID jti) {
        Instant ahora = Instant.now();
        return Jwts.builder().subject(usuarioId.toString()).id(jti.toString()).claim("sid", sesionId.toString()).claim("roles", roles)
                .issuedAt(Date.from(ahora)).expiration(Date.from(ahora.plus(propiedades.accessTokenMinutos(), ChronoUnit.MINUTES))).signWith(llave()).compact();
    }
    public Claims validar(String token) { return Jwts.parser().verifyWith(llave()).build().parseSignedClaims(token).getPayload(); }
    public Instant expiracion(Claims claims) { return claims.getExpiration().toInstant(); }
    private SecretKey llave() {
        byte[] secreto = decodificarSecreto(propiedades.secret());
        if (secreto.length < 32) throw new IllegalStateException("JWT_SECRET debe contener al menos 32 bytes codificados.");
        return Keys.hmacShaKeyFor(secreto);
    }
    private byte[] decodificarSecreto(String secreto) {
        try { return Decoders.BASE64.decode(secreto); }
        catch (DecodingException excepcion) { LOG.info("evento=jwt_secreto_base64url_detectado"); return Decoders.BASE64URL.decode(secreto); }
    }
}
