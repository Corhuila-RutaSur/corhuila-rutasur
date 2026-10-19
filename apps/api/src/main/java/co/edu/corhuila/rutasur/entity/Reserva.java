package co.edu.corhuila.rutasur.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/** Cabecera persistente de una compra configurable para una sola salida. */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "reserva")
public class Reserva {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 30) private String codigo;
    @Column(name = "usuario_id", nullable = false) private Long usuarioId;
    @Column(name = "salida_experiencia_id", nullable = false) private Long salidaExperienciaId;
    @Enumerated(EnumType.STRING) @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, columnDefinition = "estado_reserva") private EstadoReserva estado;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal subtotal;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal descuento;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal total;
    @Column(name = "fecha_reserva", nullable = false) private OffsetDateTime fechaReserva;
    @Column(length = 1000) private String observaciones;

    /** Inicializa los valores inmutables de una reserva nueva. */
    @PrePersist
    void inicializarReserva() {
        fechaReserva = OffsetDateTime.now();
        estado = EstadoReserva.PENDIENTE;
        descuento = BigDecimal.ZERO;
    }
}
