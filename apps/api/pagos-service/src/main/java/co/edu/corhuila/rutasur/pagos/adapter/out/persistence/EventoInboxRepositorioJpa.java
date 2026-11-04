package co.edu.corhuila.rutasur.pagos.adapter.out.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Acceso técnico al Inbox de Pagos.
 */
interface EventoInboxRepositorioJpa extends JpaRepository<EventoInboxEntidad, UUID> {
}
