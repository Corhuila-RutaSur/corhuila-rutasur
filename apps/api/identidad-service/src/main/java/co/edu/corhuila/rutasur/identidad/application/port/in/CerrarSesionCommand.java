package co.edu.corhuila.rutasur.identidad.application.port.in;

import java.util.UUID;

/**
 * Datos para cerrar explícitamente la sesión asociada a un access token vigente.
 */
public record CerrarSesionCommand(String accessToken, UUID correlacionId) {
}
