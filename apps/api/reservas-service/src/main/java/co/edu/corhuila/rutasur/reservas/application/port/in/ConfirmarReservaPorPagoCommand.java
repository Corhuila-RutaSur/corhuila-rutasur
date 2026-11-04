package co.edu.corhuila.rutasur.reservas.application.port.in;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Datos recibidos desde Pagos para confirmar una reserva que sigue pendiente.
 */
public record ConfirmarReservaPorPagoCommand(UUID eventoId, UUID pagoId, UUID reservaId,
                                             BigDecimal monto, String moneda, String referenciaProveedor,
                                             UUID correlacionId) {
}
