package co.edu.corhuila.rutasur.pagos.domain.model;

import java.math.*;
import java.util.*;

/**
 * Pago simulado, idempotente por correlación, de una reserva externa.
 */
public record Pago(UUID id, UUID reservaIdExterna, UUID correlacionId, BigDecimal monto, String moneda, String estado,
                   String referenciaProveedor) {
}
