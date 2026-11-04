package co.edu.corhuila.rutasur.reservas.application.port.in;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Datos del resultado de pago que exige compensar una reserva pendiente.
 */
public record CancelarReservaPorPagoCommand(UUID eventoId, UUID pagoId, UUID reservaId, BigDecimal monto,
                                            String moneda, String referenciaProveedor, String motivo,
                                            UUID correlacionId) {
}
