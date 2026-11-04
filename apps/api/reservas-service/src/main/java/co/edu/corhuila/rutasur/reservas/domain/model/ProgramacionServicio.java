package co.edu.corhuila.rutasur.reservas.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Servicio de Catálogo habilitado, con precio local, para una salida concreta.
 */
public record ProgramacionServicio(UUID id, UUID salidaId, UUID servicioIdExterno, String nombre, BigDecimal precio,
                                   int cuposTotales, int cuposDisponibles, boolean activo) {
}
