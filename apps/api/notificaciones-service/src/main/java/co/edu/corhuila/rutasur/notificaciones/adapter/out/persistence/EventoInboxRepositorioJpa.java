package co.edu.corhuila.rutasur.notificaciones.adapter.out.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio Inbox local.
 */
interface EventoInboxRepositorioJpa extends JpaRepository<EventoInboxEntidad, UUID> {
}
