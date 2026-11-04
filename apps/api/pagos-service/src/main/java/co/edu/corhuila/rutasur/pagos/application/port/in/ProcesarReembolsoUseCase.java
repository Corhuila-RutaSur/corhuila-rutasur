package co.edu.corhuila.rutasur.pagos.application.port.in;

/**
 * Procesa de forma idempotente una cancelación que puede requerir reembolso.
 */
public interface ProcesarReembolsoUseCase {
    void procesar(ProcesarReembolsoCommand comando);
}
