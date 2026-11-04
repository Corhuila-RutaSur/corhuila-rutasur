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
 * Entidad privada de persistencia para la tabla experiencia.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "experiencia")
class ExperienciaEntidad {
    @Id
    private UUID id;
    @Column(name = "tipo_experiencia_id")
    private UUID tipoExperienciaId;
    private String tipo;
    private String nombre;
    private String destino;
    private String descripcion;
    @Column(name = "duracion_horas")
    private int duracionHoras;
    private String recomendaciones;
    private boolean activa;
    @Column(name = "creado_en")
    private OffsetDateTime creadoEn;
    @Column(name = "actualizado_en")
    private OffsetDateTime actualizadoEn;
}
