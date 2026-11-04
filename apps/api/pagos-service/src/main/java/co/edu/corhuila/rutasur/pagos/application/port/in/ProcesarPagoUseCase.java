package co.edu.corhuila.rutasur.pagos.application.port.in;

import co.edu.corhuila.rutasur.pagos.domain.model.Pago;

/**
 * Puerto de entrada para procesar un pago simulado.
 */
public interface ProcesarPagoUseCase {
    Pago procesar(ProcesarPagoCommand c);
}
