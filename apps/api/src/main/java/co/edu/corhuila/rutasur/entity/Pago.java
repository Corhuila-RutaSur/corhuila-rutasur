package co.edu.corhuila.rutasur.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/** Registro persistente de un intento de pago, simulado en la primera versión. */
@Entity @Getter @Setter @NoArgsConstructor @Table(name = "pago")
public class Pago {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "reserva_id", nullable = false) private Long reservaId;
    @Column(nullable = false, length = 100) private String referencia;
    @Column(name = "metodo_pago", nullable = false, length = 50) private String metodoPago;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal valor;
    @Enumerated(EnumType.STRING) @JdbcTypeCode(SqlTypes.NAMED_ENUM) @Column(nullable = false, columnDefinition = "estado_pago") private EstadoPago estado;
    @Column(name = "fecha_pago") private OffsetDateTime fechaPago;
    @Column(name = "respuesta_pasarela", columnDefinition = "text") private String respuestaPasarela;
    @Column(name = "creado_en", nullable = false) private OffsetDateTime creadoEn;
    @PrePersist void inicializarFecha() { creadoEn = OffsetDateTime.now(); }
}
