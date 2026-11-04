package co.edu.corhuila.rutasur.reservas.adapter.out.persistence;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio interno de participantes.
 */
interface ParticipanteReservaRepositorioJpa extends JpaRepository<ParticipanteReservaEntidad, UUID> {
}
