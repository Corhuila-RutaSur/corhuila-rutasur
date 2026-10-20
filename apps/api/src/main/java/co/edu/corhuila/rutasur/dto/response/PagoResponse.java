package co.edu.corhuila.rutasur.dto.response;

import co.edu.corhuila.rutasur.entity.EstadoPago;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

/** Resultado seguro de un pago registrado. */
public record PagoResponse(Long id, Long reservaId, String referencia, String metodoPago, BigDecimal valor, EstadoPago estado, OffsetDateTime fechaPago) { }
