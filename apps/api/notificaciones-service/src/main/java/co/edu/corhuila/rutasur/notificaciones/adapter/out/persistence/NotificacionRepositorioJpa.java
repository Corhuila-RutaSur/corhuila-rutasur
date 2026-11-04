package co.edu.corhuila.rutasur.notificaciones.adapter.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA local del microservicio.
 */
interface NotificacionRepositorioJpa extends JpaRepository<NotificacionEntidad, UUID> {
    List<NotificacionEntidad> findAllByDestinatarioOrderByCreadaEnDesc(String destinatario);

    Optional<NotificacionEntidad> findByIdAndDestinatario(UUID id, String destinatario);
}
