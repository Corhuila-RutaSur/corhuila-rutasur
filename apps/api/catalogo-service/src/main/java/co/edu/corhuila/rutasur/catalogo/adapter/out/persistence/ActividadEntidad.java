package co.edu.corhuila.rutasur.catalogo.adapter.out.persistence;

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
 * Entidad privada JPA para actividades de una experiencia.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "actividad")
class ActividadEntidad {
    @Id
    private UUID id;
    @Column(name = "experiencia_id")
    private UUID experienciaId;
    private String nombre;
    private String descripcion;
    @Column(name = "duracion_horas")
    private int duracionHoras;
    private boolean activa;
    @Column(name = "creado_en")
    private OffsetDateTime creadoEn;
    @Column(name = "actualizado_en")
    private OffsetDateTime actualizadoEn;
}
