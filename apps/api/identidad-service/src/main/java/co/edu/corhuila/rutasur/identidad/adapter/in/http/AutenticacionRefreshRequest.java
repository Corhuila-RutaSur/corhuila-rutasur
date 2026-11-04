package co.edu.corhuila.rutasur.identidad.adapter.in.http;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Solicitud de renovación con refresh token emitido previamente por Identidad.
 */
public record AutenticacionRefreshRequest(
        @NotBlank(message = "autenticacion.refresh.obligatorio") @Size(max = 500, message = "autenticacion.refresh.longitud") String refreshToken) {
}
