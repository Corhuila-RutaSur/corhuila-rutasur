package co.edu.corhuila.rutasur.repository;

import co.edu.corhuila.rutasur.entity.DetalleReserva;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Persiste servicios seleccionados dentro de una reserva.
 */
public interface DetalleReservaRepositorio extends JpaRepository<DetalleReserva, Long> {
    List<DetalleReserva> findByReservaId(Long reservaId);
}
