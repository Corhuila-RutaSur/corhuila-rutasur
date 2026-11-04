package co.edu.corhuila.rutasur.reservas.application.port.in;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Datos para habilitar una actividad de Catálogo en una salida.
 */
public record ProgramarActividadCommand(UUID actividadIdExterna, String nombre, BigDecimal precio, UUID correlacionId) {
}
