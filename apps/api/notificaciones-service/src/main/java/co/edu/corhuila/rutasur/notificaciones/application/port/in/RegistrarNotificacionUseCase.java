package co.edu.corhuila.rutasur.notificaciones.application.port.in;

/**
 * Registra una notificación derivada de un evento de negocio.
 */
public interface RegistrarNotificacionUseCase {
    void registrar(RegistrarNotificacionCommand comando);
}
