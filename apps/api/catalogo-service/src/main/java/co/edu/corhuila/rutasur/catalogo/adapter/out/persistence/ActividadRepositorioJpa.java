package co.edu.corhuila.rutasur.catalogo.adapter.out.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA privado de actividades.
 */
interface ActividadRepositorioJpa extends JpaRepository<ActividadEntidad, UUID> {
    boolean existsByExperienciaIdAndNombreIgnoreCaseAndActivaTrue(UUID experienciaId, String nombre);

    List<ActividadEntidad> findAllByExperienciaIdAndActivaTrueOrderByNombreAsc(UUID experienciaId);
}
