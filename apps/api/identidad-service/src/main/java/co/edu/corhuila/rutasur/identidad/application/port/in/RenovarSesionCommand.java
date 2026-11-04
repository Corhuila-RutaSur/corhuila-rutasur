package co.edu.corhuila.rutasur.identidad.application.port.in;

import java.util.UUID;

/**
 * Datos mínimos para renovar una sesión mediante un refresh token de único uso.
 */
public record RenovarSesionCommand(String refreshToken, UUID correlacionId) {
}
