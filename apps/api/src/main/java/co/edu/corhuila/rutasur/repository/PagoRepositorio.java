package co.edu.corhuila.rutasur.repository;

import co.edu.corhuila.rutasur.entity.EstadoPago;
import co.edu.corhuila.rutasur.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;

/** Acceso persistente a pagos registrados por reserva. */
public interface PagoRepositorio extends JpaRepository<Pago, Long> {
    boolean existsByReservaIdAndEstado(Long reservaId, EstadoPago estado);
}
