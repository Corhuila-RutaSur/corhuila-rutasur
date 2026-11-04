package co.edu.corhuila.rutasur.catalogo.adapter.out.persistence;

import java.util.UUID;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;

/**
 * Repositorio privado del adaptador JPA de experiencias.
 */
interface ExperienciaRepositorioJpa extends JpaRepository<ExperienciaEntidad, UUID> {
    List<ExperienciaEntidad> findAllByActivaTrueOrderByNombreAsc();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    java.util.Optional<ExperienciaEntidad> findWithLockById(UUID identificador);
}
