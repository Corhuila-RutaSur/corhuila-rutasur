package co.edu.corhuila.rutasur.repository;

import co.edu.corhuila.rutasur.entity.ServicioExperiencia;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ServicioExperienciaRepositorio extends JpaRepository<ServicioExperiencia, Long> {
    List<ServicioExperiencia> findByExperienciaIdAndActivoTrueOrderByNombreAsc(Long experienciaId);

    Optional<ServicioExperiencia> findByIdAndExperienciaIdAndActivoTrue(Long id, Long experienciaId);

    boolean existsByExperienciaIdAndNombreIgnoreCase(Long experienciaId, String nombre);
}
