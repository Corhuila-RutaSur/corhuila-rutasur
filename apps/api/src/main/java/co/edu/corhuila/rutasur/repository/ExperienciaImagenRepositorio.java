package co.edu.corhuila.rutasur.repository;
import co.edu.corhuila.rutasur.entity.ExperienciaImagen; import java.util.List; import org.springframework.data.jpa.repository.JpaRepository;
/** Acceso a la galería ordenada de una experiencia. */
public interface ExperienciaImagenRepositorio extends JpaRepository<ExperienciaImagen,Long>{ List<ExperienciaImagen> findByExperienciaIdOrderByPrincipalDescOrdenAsc(Long experienciaId); void deleteByExperienciaId(Long experienciaId); }
