package co.edu.corhuila.rutasur.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Fecha comercial y operativa de una experiencia.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "salida_experiencia")
public class SalidaExperiencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "experiencia_id", nullable = false)
    private Long experienciaId;
    @Column(name = "fecha_hora_inicio", nullable = false)
    private OffsetDateTime fechaHoraInicio;
    @Column(name = "fecha_hora_fin")
    private OffsetDateTime fechaHoraFin;
    @Column(name = "cupos_totales", nullable = false)
    private Integer cuposTotales;
    @Column(name = "cupos_disponibles", nullable = false)
    private Integer cuposDisponibles;
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, columnDefinition = "estado_salida")
    private EstadoSalida estado = EstadoSalida.PROGRAMADA;
    @Column(columnDefinition = "text")
    private String observaciones;
}
