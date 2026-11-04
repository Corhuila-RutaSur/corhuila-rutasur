package co.edu.corhuila.rutasur.identidad.application.port.in;

import java.util.UUID;

/**
 * Datos de entrada para autenticar una identidad por correo y contraseña.
 */
public record IniciarSesionCommand(String correo, String contrasenia, String ipOrigen, String agenteUsuario,
                                   UUID correlacionId) {
}
