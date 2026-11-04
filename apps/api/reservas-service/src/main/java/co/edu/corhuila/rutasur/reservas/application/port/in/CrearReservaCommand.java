package co.edu.corhuila.rutasur.reservas.application.port.in;

import java.util.*;

/**
 * Datos mínimos para reservar cupos y una selección común de la salida.
 */
public record CrearReservaCommand(UUID usuarioIdExterno, UUID salidaId, int cantidadParticipantes,
                                  List<ParticipanteReservaCommand> participantes, List<UUID> programacionesServicio,
                                  List<UUID> programacionesActividad, UUID correlacionId) {
}
