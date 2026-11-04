package co.edu.corhuila.rutasur.reservas.adapter.out.persistence;

import co.edu.corhuila.rutasur.reservas.application.port.out.ProgramacionServicioPuertoSalida;
import co.edu.corhuila.rutasur.reservas.domain.model.ProgramacionServicio;

import java.util.*;

import lombok.*;
import org.springframework.stereotype.Component;

/**
 * Adaptador JPA para servicios habilitados por salida.
 */
@Component
@RequiredArgsConstructor
public class ProgramacionServicioPersistenciaAdaptador implements ProgramacionServicioPuertoSalida {
    private final ProgramacionServicioRepositorioJpa repositorio;
    private final ProgramacionServicioPersistenciaMapper mapper;

    @Override
    public ProgramacionServicio guardar(ProgramacionServicio valor) {
        return mapper.aDominio(repositorio.save(mapper.aEntidad(valor)));
    }

    @Override
    public List<ProgramacionServicio> buscarActivas(UUID salidaId, List<UUID> ids) {
        return ids.isEmpty() ? List.of() : repositorio.findAllBySalidaIdAndIdInAndActivoTrue(salidaId, ids).stream().map(mapper::aDominio).toList();
    }
}
