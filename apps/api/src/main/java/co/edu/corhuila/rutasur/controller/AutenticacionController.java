package co.edu.corhuila.rutasur.controller;

import co.edu.corhuila.rutasur.api.AutenticacionApi;
import co.edu.corhuila.rutasur.config.security.UsuarioAutenticado;
import co.edu.corhuila.rutasur.dto.request.LoginRequest;
import co.edu.corhuila.rutasur.dto.request.RefreshTokenRequest;
import co.edu.corhuila.rutasur.dto.response.TokenResponse;
import co.edu.corhuila.rutasur.service.AutenticacionServicio;
import co.edu.corhuila.rutasur.service.JwtServicio;
import io.jsonwebtoken.Claims;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

/**
 * Adaptador REST de sesión.
 */
@RestController
@RequiredArgsConstructor
public class AutenticacionController implements AutenticacionApi {
    private final AutenticacionServicio servicio;
    private final JwtServicio jwt;

    @Override
    public ResponseEntity<TokenResponse> login(LoginRequest solicitud) {
        return ResponseEntity.ok(servicio.login(solicitud));
    }

    @Override
    public ResponseEntity<TokenResponse> renovar(RefreshTokenRequest solicitud) {
        return ResponseEntity.ok(servicio.renovar(solicitud));
    }

    @Override
    public ResponseEntity<Void> logout() {
        Authentication autenticacion = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        UsuarioAutenticado principal = (UsuarioAutenticado) autenticacion.getPrincipal();
        String token = ((jakarta.servlet.http.HttpServletRequest) org.springframework.web.context.request.RequestContextHolder.currentRequestAttributes().resolveReference(org.springframework.web.context.request.RequestAttributes.REFERENCE_REQUEST)).getHeader("Authorization").substring(7);
        Claims claims = jwt.validar(token);
        servicio.logout(principal.usuarioId(), principal.sesionId(), UUID.fromString(claims.getId()), OffsetDateTime.ofInstant(jwt.expiracion(claims), java.time.ZoneOffset.UTC));
        return ResponseEntity.noContent().build();
    }
}
