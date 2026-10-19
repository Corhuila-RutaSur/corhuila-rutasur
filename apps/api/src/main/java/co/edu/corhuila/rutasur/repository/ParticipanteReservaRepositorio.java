package co.edu.corhuila.rutasur.repository;
import co.edu.corhuila.rutasur.entity.ParticipanteReserva;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
/** Persiste participantes de las reservas. */
public interface ParticipanteReservaRepositorio extends JpaRepository<ParticipanteReserva, Long> {
    List<ParticipanteReserva> findByReservaId(Long reservaId);
}
