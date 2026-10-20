package co.edu.corhuila.rutasur.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Imagen ordenada que forma parte de la galería de una experiencia.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "experiencia_imagen")
public class ExperienciaImagen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "experiencia_id", nullable = false)
    private Long experienciaId;
    @Column(nullable = false, length = 500)
    private String url;
    @Column(length = 255)
    private String descripcion;
    @Column(nullable = false)
    private Integer orden = 1;
    @Column(nullable = false)
    private boolean principal;
}
