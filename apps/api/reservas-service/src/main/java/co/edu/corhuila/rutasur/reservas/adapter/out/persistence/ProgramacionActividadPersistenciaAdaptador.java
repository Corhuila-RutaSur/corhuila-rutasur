package co.edu.corhuila.rutasur.reservas.adapter.out.persistence;

import co.edu.corhuila.rutasur.reservas.application.port.out.ProgramacionActividadPuertoSalida;
import co.edu.corhuila.rutasur.reservas.domain.model.ProgramacionActividad;

import java.util.*;

import lombok.*;
import org.springframework.stereotype.Component;

/**
 * Adaptador JPA para actividades habilitadas por salida.
 */
@Component
@RequiredArgsConstructor
public class ProgramacionActividadPersistenciaAdaptador implements ProgramacionActividadPuertoSalida {
    private final ProgramacionActividadRepositorioJpa repositorio;

    @Override
    public ProgramacionActividad guardar(ProgramacionActividad p) {
        ProgramacionActividadEntidad e = new ProgramacionActividadEntidad();
        e.setId(p.id() == null ? java.util.UUID.randomUUID() : p.id());
        e.setSalidaId(p.salidaId());
        e.setActividadIdExterna(p.actividadIdExterna());
        e.setNombre(p.nombre());
        e.setPrecio(p.precio());
        e.setActivo(p.activo());
        e = repositorio.save(e);
        return new ProgramacionActividad(e.getId(), e.getSalidaId(), e.getActividadIdExterna(), e.getNombre(), e.getPrecio(), e.isActivo());
    }

    @Override
    public List<ProgramacionActividad> buscarActivas(UUID salidaId, List<UUID> ids) {
        return ids.isEmpty() ? List.of() : repositorio.findAllBySalidaIdAndIdInAndActivoTrue(salidaId, ids).stream().map(e -> new ProgramacionActividad(e.getId(), e.getSalidaId(), e.getActividadIdExterna(), e.getNombre(), e.getPrecio(), e.isActivo())).toList();
    }
}
