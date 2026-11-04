package co.edu.corhuila.rutasur.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Token opaco para renovar una sesión.
 */
public record RefreshTokenRequest(@NotBlank String refreshToken) {
}
