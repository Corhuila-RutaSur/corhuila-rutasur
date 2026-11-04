package co.edu.corhuila.rutasur.pagos.adapter.in.http;

import java.math.*;
import java.util.*;

/**
 * Representación HTTP del resultado de un pago.
 */
public record PagoResponse(UUID id, UUID reservaIdExterna, BigDecimal monto, String moneda, String estado,
                           String referenciaProveedor) {
}
