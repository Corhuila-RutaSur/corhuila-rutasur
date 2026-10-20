package co.edu.corhuila.rutasur.repository;
import co.edu.corhuila.rutasur.entity.*; import java.util.List; import java.util.Optional; import org.springframework.data.jpa.repository.JpaRepository;
public interface SalidaExperienciaRepositorio extends JpaRepository<SalidaExperiencia,Long>{List<SalidaExperiencia> findByExperienciaIdOrderByFechaHoraInicioAsc(Long experienciaId);List<SalidaExperiencia> findByExperienciaIdAndEstadoOrderByFechaHoraInicioAsc(Long experienciaId,EstadoSalida estado);Optional<SalidaExperiencia> findByIdAndExperienciaId(Long id,Long experienciaId);}
