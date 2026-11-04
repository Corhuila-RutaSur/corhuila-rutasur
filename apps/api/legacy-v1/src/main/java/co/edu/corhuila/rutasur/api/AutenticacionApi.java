package co.edu.corhuila.rutasur.api;

import co.edu.corhuila.rutasur.dto.request.LoginRequest;
import co.edu.corhuila.rutasur.dto.request.RefreshTokenRequest;
import co.edu.corhuila.rutasur.dto.response.TokenResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Contrato HTTP de autenticación.
 */
public interface AutenticacionApi {
    @PostMapping("/api/v1/public/autenticacion/login")
    ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest solicitud);

    @PostMapping("/api/v1/public/autenticacion/refresh")
    ResponseEntity<TokenResponse> renovar(@Valid @RequestBody RefreshTokenRequest solicitud);

    @PostMapping("/api/v1/autenticacion/logout")
    ResponseEntity<Void> logout();
}
