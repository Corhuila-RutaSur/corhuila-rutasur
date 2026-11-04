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
 * Entidad privada JPA para el catálogo de tipos de experiencia.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "tipo_experiencia")
class TipoExperienciaEntidad {
    @Id
    private UUID id;
    private String nombre;
    private String descripcion;
    private boolean activo;
    @Column(name = "creado_en")
    private OffsetDateTime creadoEn;
    @Column(name = "actualizado_en")
    private OffsetDateTime actualizadoEn;
}
