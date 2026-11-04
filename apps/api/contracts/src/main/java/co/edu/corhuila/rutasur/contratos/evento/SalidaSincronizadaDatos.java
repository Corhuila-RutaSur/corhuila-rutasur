package co.edu.corhuila.rutasur.contratos.evento;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Vista pública de una salida reservable distribuida por Reservas.
 */
public record SalidaSincronizadaDatos(UUID salidaId, UUID experienciaIdExterna, OffsetDateTime fechaHoraInicio,
                                      int cuposDisponibles, String estado) {
}
