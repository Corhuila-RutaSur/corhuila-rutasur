package co.edu.corhuila.rutasur.repository;
import co.edu.corhuila.rutasur.entity.TipoExperiencia;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
/** Consulta tipos turísticos activos. */
public interface TipoExperienciaRepositorio extends JpaRepository<TipoExperiencia, Long> {
    Optional<TipoExperiencia> findByIdAndActivoTrue(Long id);
}
