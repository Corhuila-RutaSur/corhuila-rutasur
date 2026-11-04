package co.edu.corhuila.rutasur.reservas.adapter.out.persistence;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA interno de servicios programados.
 */
interface ProgramacionServicioRepositorioJpa extends JpaRepository<ProgramacionServicioEntidad, UUID> {
    List<ProgramacionServicioEntidad> findAllBySalidaIdAndIdInAndActivoTrue(UUID salidaId, List<UUID> ids);
}
