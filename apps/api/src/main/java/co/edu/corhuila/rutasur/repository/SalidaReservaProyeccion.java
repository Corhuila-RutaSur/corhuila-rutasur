package co.edu.corhuila.rutasur.repository;

/**
 * Datos de disponibilidad de una salida requeridos durante la reserva.
 */
public interface SalidaReservaProyeccion {
    Long getId();

    Integer getCuposDisponibles();

    String getEstado();
}
