package co.edu.corhuila.rutasur.reservas.application.port.in;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Datos para habilitar un servicio de Catálogo en una salida.
 */
public record ProgramarServicioCommand(UUID servicioIdExterno, String nombre, BigDecimal precio, int cuposTotales, UUID correlacionId) {
}
