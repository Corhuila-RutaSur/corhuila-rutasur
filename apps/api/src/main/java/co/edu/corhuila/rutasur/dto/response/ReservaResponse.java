package co.edu.corhuila.rutasur.dto.response;

import co.edu.corhuila.rutasur.entity.EstadoReserva;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

/** Resumen seguro de una reserva persistida. */
public record ReservaResponse(Long id, String codigo, Long usuarioId, Long salidaExperienciaId,
        EstadoReserva estado, BigDecimal subtotal, BigDecimal descuento, BigDecimal total,
        OffsetDateTime fechaReserva, String observaciones) { }
