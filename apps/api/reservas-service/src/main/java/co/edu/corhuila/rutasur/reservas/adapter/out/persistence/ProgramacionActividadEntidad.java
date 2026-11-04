package co.edu.corhuila.rutasur.reservas.adapter.out.persistence;

import jakarta.persistence.*;

import java.math.*;
import java.util.*;

import lombok.*;

/**
 * Entidad JPA privada para actividades configuradas en una salida.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "programacion_actividad")
class ProgramacionActividadEntidad {
    @Id
    private UUID id;
    @Column(name = "salida_id")
    private UUID salidaId;
    @Column(name = "actividad_id_externa")
    private UUID actividadIdExterna;
    private String nombre;
    private BigDecimal precio;
    private boolean activo;
}
