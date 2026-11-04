package co.edu.corhuila.rutasur.reservas.adapter.in.http;

import java.math.*;
import java.util.*;

/**
 * Vista HTTP de una actividad habilitada para una salida.
 */
public record ProgramacionActividadResponse(UUID id, UUID salidaId, UUID actividadIdExterna, String nombre,
                                            BigDecimal precio, boolean activo) {
}
