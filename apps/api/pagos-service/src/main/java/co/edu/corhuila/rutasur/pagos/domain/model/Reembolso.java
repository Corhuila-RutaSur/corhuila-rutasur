package co.edu.corhuila.rutasur.pagos.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Devolución financiera causada por la cancelación de una reserva ya pagada.
 */
public record Reembolso(UUID id, UUID pagoId, BigDecimal monto, String estado, String motivo) {
}
