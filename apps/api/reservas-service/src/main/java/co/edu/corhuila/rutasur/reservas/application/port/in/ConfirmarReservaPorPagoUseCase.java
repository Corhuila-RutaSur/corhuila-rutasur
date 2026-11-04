package co.edu.corhuila.rutasur.reservas.application.port.in;

/**
 * Confirma de forma idempotente una reserva luego del hecho PagoAprobado.
 */
public interface ConfirmarReservaPorPagoUseCase {
    void confirmar(ConfirmarReservaPorPagoCommand comando);
}
