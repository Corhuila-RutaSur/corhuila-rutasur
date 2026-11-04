package co.edu.corhuila.rutasur.reservas.application.port.out;

import co.edu.corhuila.rutasur.reservas.domain.model.ProgramacionActividad;

import java.util.*;

/**
 * Puerto de persistencia para actividades habilitadas por salida.
 */
public interface ProgramacionActividadPuertoSalida {
    ProgramacionActividad guardar(ProgramacionActividad programacion);

    List<ProgramacionActividad> buscarActivas(UUID salidaId, List<UUID> ids);
}
