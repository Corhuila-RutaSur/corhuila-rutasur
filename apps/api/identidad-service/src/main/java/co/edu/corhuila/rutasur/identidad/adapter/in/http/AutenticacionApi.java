package co.edu.corhuila.rutasur.identidad.adapter.in.http;

import jakarta.validation.Valid;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Contrato HTTP público de inicio de sesión de Identidad.
 */
@RequestMapping("/api/v2/public/autenticacion")
public interface AutenticacionApi {

    /**
     * Autentica al usuario y devuelve una sesión con access y refresh tokens.
     */
    @PostMapping("/login")
    ResponseEntity<RespuestaGeneral<AutenticacionLoginResponse>> iniciarSesion(
            @Valid @RequestBody AutenticacionLoginRequest solicitud,
            @RequestHeader(value = "X-Correlation-Id", required = false) UUID correlacionId,
            @RequestHeader(value = "X-Forwarded-For", required = false) String ipOrigen,
            @RequestHeader(value = "User-Agent", required = false) String agenteUsuario);

    /**
     * Rota un refresh token de único uso y emite una nueva pareja de tokens.
     */
    @PostMapping("/refresh")
    ResponseEntity<RespuestaGeneral<AutenticacionLoginResponse>> renovarSesion(
            @Valid @RequestBody AutenticacionRefreshRequest solicitud,
            @RequestHeader(value = "X-Correlation-Id", required = false) UUID correlacionId);
}
