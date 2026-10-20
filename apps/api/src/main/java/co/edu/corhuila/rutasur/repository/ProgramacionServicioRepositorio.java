package co.edu.corhuila.rutasur.repository;
import co.edu.corhuila.rutasur.entity.ProgramacionServicio; import java.util.*; import org.springframework.data.jpa.repository.JpaRepository;
public interface ProgramacionServicioRepositorio extends JpaRepository<ProgramacionServicio,Long>{List<ProgramacionServicio> findBySalidaExperienciaIdAndActivoTrue(Long salidaId);Optional<ProgramacionServicio> findByIdAndSalidaExperienciaIdAndActivoTrue(Long id,Long salidaId);boolean existsBySalidaExperienciaIdAndServicioExperienciaId(Long salidaId,Long servicioId);}
