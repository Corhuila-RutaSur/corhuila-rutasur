package co.edu.corhuila.rutasur.identidad.application.port.in;

import co.edu.corhuila.rutasur.identidad.domain.model.SesionIniciada;

/**
 * Caso de uso de rotación segura de refresh token.
 */
public interface RenovarSesionUseCase {

    /**
     * Renueva una sesión una sola vez por cada refresh token emitido.
     */
    SesionIniciada renovar(RenovarSesionCommand comando);
}
