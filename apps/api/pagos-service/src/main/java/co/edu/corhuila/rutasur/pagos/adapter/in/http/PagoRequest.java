package co.edu.corhuila.rutasur.pagos.adapter.in.http;

import co.edu.corhuila.rutasur.pagos.application.port.in.ResultadoPagoSimulado;
import jakarta.validation.constraints.*;

import java.math.*;
import java.util.*;

/**
 * Solicitud de pago simulado. Si no se informa resultado, el simulador aprueba el pago.
 */
public record PagoRequest(@NotNull UUID reservaIdExterna, @NotNull @DecimalMin("0.01") BigDecimal monto,
                          ResultadoPagoSimulado resultado) {
}
