package co.edu.corhuila.rutasur.pagos.adapter.out.persistence;

import jakarta.persistence.*;

import java.math.*;
import java.time.*;
import java.util.*;

import lombok.*;

/**
 * Entidad privada JPA del pago.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "pago")
class PagoEntidad {
    @Id
    private UUID id;
    @Column(name = "reserva_id_externa")
    private UUID reservaIdExterna;
    @Column(name = "correlacion_id")
    private UUID correlacionId;
    private BigDecimal monto;
    private String moneda;
    private String estado;
    @Column(name = "referencia_proveedor")
    private String referenciaProveedor;
    @Column(name = "creado_en")
    private OffsetDateTime creadoEn;
}
