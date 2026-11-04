package co.edu.corhuila.rutasur.contratos.evento;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Datos de un pago rechazado o expirado que exige compensar la reserva pendiente.
 */
public record PagoNoAprobadoDatos(UUID pagoId, UUID reservaIdExterna, BigDecimal monto, String moneda,
                                  String referenciaProveedor, String motivo) {
}
