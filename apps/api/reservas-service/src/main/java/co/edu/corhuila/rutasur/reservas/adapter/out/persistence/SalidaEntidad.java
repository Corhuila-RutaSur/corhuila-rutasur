package co.edu.corhuila.rutasur.reservas.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad privada JPA para salidas programadas.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "salida")
class SalidaEntidad {
    @Id
    private UUID id;
    @Column(name = "experiencia_id_externa")
    private UUID experienciaIdExterna;
    @Column(name = "experiencia_nombre")
    private String experienciaNombre;
    @Column(name = "fecha_hora_inicio")
    private OffsetDateTime fechaHoraInicio;
    @Column(name = "cupos_totales")
    private int cuposTotales;
    @Column(name = "cupos_disponibles")
    private int cuposDisponibles;
    private String estado;
    @Column(name = "creado_en")
    private OffsetDateTime creadoEn;
}
