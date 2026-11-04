package co.edu.corhuila.rutasur.reservas.adapter.out.persistence;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.*;

/**
 * Entidad JPA privada para servicios configurados en una salida.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "programacion_servicio")
class ProgramacionServicioEntidad {
    @Id
    private UUID id;
    @Column(name = "salida_id")
    private UUID salidaId;
    @Column(name = "servicio_id_externo")
    private UUID servicioIdExterno;
    private String nombre;
    private BigDecimal precio;
    @Column(name = "cupos_totales") private int cuposTotales;
    @Column(name = "cupos_disponibles") private int cuposDisponibles;
    private boolean activo;
}
