package co.edu.corhuila.rutasur.repository;

import co.edu.corhuila.rutasur.entity.DetalleParticipante;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Persiste asignaciones de participantes a servicios comprados.
 */
public interface DetalleParticipanteRepositorio extends JpaRepository<DetalleParticipante, Long> {
}
