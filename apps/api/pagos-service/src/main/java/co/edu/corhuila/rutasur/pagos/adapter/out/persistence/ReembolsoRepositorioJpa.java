package co.edu.corhuila.rutasur.pagos.adapter.out.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Acceso técnico a devoluciones de pago.
 */
interface ReembolsoRepositorioJpa extends JpaRepository<ReembolsoEntidad, UUID> {
    boolean existsByPagoId(UUID pagoId);
}
