package co.edu.corhuila.rutasur.catalogo.adapter.out.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio interno de persistencia para tipos de experiencia.
 */
interface TipoExperienciaRepositorioJpa extends JpaRepository<TipoExperienciaEntidad, UUID> {
    boolean existsByNombreIgnoreCaseAndActivoTrue(String nombre);

    List<TipoExperienciaEntidad> findAllByActivoTrueOrderByNombreAsc();
}
