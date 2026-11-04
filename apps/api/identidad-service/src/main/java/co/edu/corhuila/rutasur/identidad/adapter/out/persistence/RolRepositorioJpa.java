package co.edu.corhuila.rutasur.identidad.adapter.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio privado para buscar roles habilitados.
 */
interface RolRepositorioJpa extends JpaRepository<RolEntidad, UUID> {
    Optional<RolEntidad> findByNombreAndActivoTrue(String nombre);

    List<RolEntidad> findAllByActivoTrueOrderByNombreAsc();
}
