package co.edu.corhuila.rutasur.reservas.adapter.out.persistence;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio privado de actividades programadas.
 */
interface ProgramacionActividadRepositorioJpa extends JpaRepository<ProgramacionActividadEntidad, UUID> {
    List<ProgramacionActividadEntidad> findAllBySalidaIdAndIdInAndActivoTrue(UUID salidaId, List<UUID> ids);
}
