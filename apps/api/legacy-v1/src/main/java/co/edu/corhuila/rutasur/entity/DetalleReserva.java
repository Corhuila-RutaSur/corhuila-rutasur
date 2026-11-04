package co.edu.corhuila.rutasur.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Servicio comprado, cantidad y precio congelado dentro de una reserva.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "detalle_reserva")
public class DetalleReserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "reserva_id", nullable = false)
    private Long reservaId;
    @Column(name = "programacion_servicio_id", nullable = false)
    private Long programacionServicioId;
    @Column(nullable = false)
    private int cantidad;
    @Column(name = "precio_unitario", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioUnitario;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;
}
