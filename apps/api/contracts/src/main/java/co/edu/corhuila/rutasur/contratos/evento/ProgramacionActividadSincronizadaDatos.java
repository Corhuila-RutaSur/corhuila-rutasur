package co.edu.corhuila.rutasur.contratos.evento;

import java.math.BigDecimal;
import java.util.UUID;

/** Actividad comercial habilitada para una salida, publicada por Reservas. */
public record ProgramacionActividadSincronizadaDatos(UUID programacionId, UUID salidaId, UUID actividadIdExterna,
                                                     String nombre, BigDecimal precio, boolean activo) {
}
