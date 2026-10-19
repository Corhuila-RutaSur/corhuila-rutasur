package co.edu.corhuila.rutasur.repository;

import co.edu.corhuila.rutasur.entity.Experiencia;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Acceso persistente al catálogo de experiencias. */
public interface ExperienciaRepositorio extends JpaRepository<Experiencia, Long> {
    Optional<Experiencia> findByIdAndActivoTrue(Long id);
    List<Experiencia> findByActivoTrueOrderByNombreAsc();
    @Modifying
    @Query("update Experiencia experiencia set experiencia.activo = false, experiencia.actualizadoEn = :fecha "
            + "where experiencia.id = :id and experiencia.activo = true")
    int inactivarPorId(@Param("id") Long id, @Param("fecha") OffsetDateTime fecha);
}
