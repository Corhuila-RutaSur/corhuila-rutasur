package co.edu.corhuila.rutasur.contratos.evento;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Datos mínimos del hecho PagoAprobado consumido por Reservas.
 */
public record PagoAprobadoDatos(UUID pagoId, UUID reservaIdExterna, BigDecimal monto, String moneda,
                                String referenciaProveedor) {
}
