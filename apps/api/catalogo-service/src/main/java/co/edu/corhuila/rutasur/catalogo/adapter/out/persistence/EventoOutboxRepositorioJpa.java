package co.edu.corhuila.rutasur.catalogo.adapter.out.persistence;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio de eventos pendientes.
 */
interface EventoOutboxRepositorioJpa extends JpaRepository<EventoOutboxEntidad, UUID> {
    List<EventoOutboxEntidad> findTop20ByPublicadoEnIsNullOrderByOcurridoEnAsc();
}
