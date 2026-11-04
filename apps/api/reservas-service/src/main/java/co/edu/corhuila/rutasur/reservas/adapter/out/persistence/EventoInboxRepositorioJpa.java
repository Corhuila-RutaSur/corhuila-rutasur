package co.edu.corhuila.rutasur.reservas.adapter.out.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Acceso técnico a la bitácora Inbox.
 */
interface EventoInboxRepositorioJpa extends JpaRepository<EventoInboxEntidad, UUID> {
}
