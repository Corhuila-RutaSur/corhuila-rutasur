package co.edu.corhuila.rutasur.reservas.application.port.out;

import co.edu.corhuila.rutasur.reservas.domain.model.ProgramacionServicio;

import java.util.*;

public interface ProgramacionServicioPuertoSalida {
    ProgramacionServicio guardar(ProgramacionServicio programacion);

    List<ProgramacionServicio> buscarActivas(UUID salidaId, List<UUID> ids);
}
