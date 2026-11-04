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

/** Entidad privada de persistencia para la galería del catálogo. */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "experiencia_imagen")
class ImagenExperienciaEntidad {
    @Id private UUID id;
    @Column(name = "experiencia_id") private UUID experienciaId;
    private String ruta;
    private boolean principal;
    private int orden;
    private boolean activa;
    @Column(name = "creado_en") private OffsetDateTime creadoEn;
    @Column(name = "actualizado_en") private OffsetDateTime actualizadoEn;
}
