package co.edu.corhuila.rutasur.reservas.adapter.out.persistence;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA interno de reservas.
 */
interface ReservaRepositorioJpa extends JpaRepository<ReservaEntidad, UUID> {
    List<ReservaEntidad> findAllByUsuarioIdExternoOrderByCreadaEnDesc(UUID usuarioIdExterno);
}
