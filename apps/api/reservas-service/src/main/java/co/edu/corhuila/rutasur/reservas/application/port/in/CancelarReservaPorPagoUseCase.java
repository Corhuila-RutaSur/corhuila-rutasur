package co.edu.corhuila.rutasur.reservas.application.port.in;

/**
 * Compensa una reserva pendiente cuando Pagos no aprueba su cobro.
 */
public interface CancelarReservaPorPagoUseCase {
    void cancelar(CancelarReservaPorPagoCommand comando);
}
