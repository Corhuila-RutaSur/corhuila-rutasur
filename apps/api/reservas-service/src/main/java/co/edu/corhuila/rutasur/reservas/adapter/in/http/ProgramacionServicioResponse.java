package co.edu.corhuila.rutasur.reservas.adapter.in.http;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Respuesta de un servicio habilitado para una salida.
 */
public record ProgramacionServicioResponse(UUID id, UUID salidaId, UUID servicioIdExterno, String nombre,
                                           BigDecimal precio, int cuposTotales, int cuposDisponibles, boolean activo) {
}
