package co.edu.corhuila.rutasur.contratos.evento;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Resultado de un reembolso simulado generado por la cancelación de una reserva pagada.
 */
public record ReembolsoConfirmadoDatos(UUID reembolsoId, UUID pagoId, UUID reservaIdExterna, BigDecimal monto,
                                       String motivo) {
}
