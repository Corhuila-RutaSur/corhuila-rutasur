package co.edu.corhuila.rutasur.reservas.adapter.out.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Acceso técnico a eventos de integración pendientes.
 */
interface EventoOutboxRepositorioJpa extends JpaRepository<EventoOutboxEntidad, UUID> {
    List<EventoOutboxEntidad> findTop20ByPublicadoEnIsNullOrderByOcurridoEnAsc();
}
