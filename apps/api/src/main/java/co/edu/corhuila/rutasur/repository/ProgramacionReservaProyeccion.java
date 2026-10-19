package co.edu.corhuila.rutasur.repository;

import java.math.BigDecimal;

/** Precio y disponibilidad de un servicio programado para una salida. */
public interface ProgramacionReservaProyeccion {
    Long getId();
    BigDecimal getPrecioUnitario();
    Integer getCuposDisponibles();
    Boolean getActivo();
}
