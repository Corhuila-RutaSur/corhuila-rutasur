package co.edu.corhuila.rutasur.pagos.adapter.out.persistence;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio interno del Outbox de Pagos.
 */
interface EventoOutboxRepositorioJpa extends JpaRepository<EventoOutboxEntidad, UUID> {
    List<EventoOutboxEntidad> findTop20ByPublicadoEnIsNullOrderByOcurridoEnAsc();
}
