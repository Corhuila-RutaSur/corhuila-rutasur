package co.edu.corhuila.rutasur.pagos.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad privada JPA del reembolso.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "reembolso")
class ReembolsoEntidad {
    @Id
    private UUID id;
    @Column(name = "pago_id")
    private UUID pagoId;
    private BigDecimal monto;
    private String estado;
    private String motivo;
    @Column(name = "creado_en")
    private OffsetDateTime creadoEn;
}
