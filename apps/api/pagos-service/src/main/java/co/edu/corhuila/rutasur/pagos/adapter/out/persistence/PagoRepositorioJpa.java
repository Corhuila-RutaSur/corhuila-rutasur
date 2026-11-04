package co.edu.corhuila.rutasur.pagos.adapter.out.persistence;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio privado de pagos.
 */
interface PagoRepositorioJpa extends JpaRepository<PagoEntidad, UUID> {
    Optional<PagoEntidad> findByCorrelacionId(UUID id);

    Optional<PagoEntidad> findFirstByReservaIdExternaAndEstadoOrderByCreadoEnDesc(UUID reservaIdExterna, String estado);
}
