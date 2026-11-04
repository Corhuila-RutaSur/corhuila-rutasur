package co.edu.corhuila.rutasur.contratos.evento;

import java.math.BigDecimal;
import java.util.UUID;

/** Servicio comercial habilitado para una salida, publicado por Reservas. */
public record ProgramacionServicioSincronizadaDatos(UUID programacionId, UUID salidaId, UUID servicioIdExterno,
                                                    String nombre, BigDecimal precio, int cuposDisponibles, boolean activo) {
}
