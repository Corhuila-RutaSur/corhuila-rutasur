package co.edu.corhuila.rutasur.reservas.adapter.out.persistence;

import jakarta.persistence.*;

import java.math.*;
import java.time.*;
import java.util.*;

import lombok.*;

/**
 * Entidad JPA privada de una reserva.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "reserva")
class ReservaEntidad {
    @Id
    private UUID id;
    private String codigo;
    @Column(name = "usuario_id_externo")
    private UUID usuarioIdExterno;
    @Column(name = "salida_id")
    private UUID salidaId;
    @Column(name = "cantidad_participantes")
    private int cantidadParticipantes;
    private BigDecimal total;
    private String estado;
    @Column(name = "correlacion_id")
    private UUID correlacionId;
    @Column(name = "creada_en")
    private OffsetDateTime creadaEn;
}
