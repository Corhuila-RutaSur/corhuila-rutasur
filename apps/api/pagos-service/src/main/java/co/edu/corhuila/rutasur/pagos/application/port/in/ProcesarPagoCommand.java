package co.edu.corhuila.rutasur.pagos.application.port.in;

import java.math.*;
import java.util.*;

/**
 * Datos del cobro simulado explícito de una reserva.
 */
public record ProcesarPagoCommand(UUID reservaIdExterna, BigDecimal monto, ResultadoPagoSimulado resultado,
                                  UUID correlacionId) {
}
