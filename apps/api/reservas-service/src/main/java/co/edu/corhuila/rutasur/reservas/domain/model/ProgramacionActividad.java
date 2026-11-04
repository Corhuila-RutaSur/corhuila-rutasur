package co.edu.corhuila.rutasur.reservas.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Actividad de Catálogo habilitada, con precio local, para una salida.
 */
public record ProgramacionActividad(UUID id, UUID salidaId, UUID actividadIdExterna, String nombre, BigDecimal precio,
                                    boolean activo) {
}
