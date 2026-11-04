package co.edu.corhuila.rutasur.catalogo.adapter.out.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio interno de servicios configurables de experiencia.
 */
interface ServicioExperienciaRepositorioJpa extends JpaRepository<ServicioExperienciaEntidad, UUID> {
    boolean existsByExperienciaIdAndNombreIgnoreCaseAndActivoTrue(UUID experienciaId, String nombre);

    List<ServicioExperienciaEntidad> findAllByExperienciaIdAndActivoTrueOrderByNombreAsc(UUID experienciaId);
}
