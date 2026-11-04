package co.edu.corhuila.rutasur.identidad.application.port.in;

/**
 * Caso de uso de cierre de sesión e invalidación de tokens.
 */
public interface CerrarSesionUseCase {

    /**
     * Revoca el access token actual e invalida la sesión que lo emitió.
     */
    void cerrar(CerrarSesionCommand comando);
}
