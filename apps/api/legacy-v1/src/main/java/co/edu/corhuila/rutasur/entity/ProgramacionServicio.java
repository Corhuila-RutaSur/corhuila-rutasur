package co.edu.corhuila.rutasur.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Precio y capacidad de un servicio dentro de una salida concreta.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "programacion_servicio")
public class ProgramacionServicio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "salida_experiencia_id", nullable = false)
    private Long salidaExperienciaId;
    @Column(name = "servicio_experiencia_id", nullable = false)
    private Long servicioExperienciaId;
    @Column(name = "precio_unitario", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioUnitario;
    @Column(name = "cupos_totales")
    private Integer cuposTotales;
    @Column(name = "cupos_disponibles")
    private Integer cuposDisponibles;
    @Column(nullable = false)
    private boolean activo = true;
}
