package co.edu.corhuila.rutasur.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Producto turístico configurable disponible en el catálogo.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "experiencia")
public class Experiencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "tipo_experiencia_id", nullable = false)
    private Long tipoExperienciaId;
    @Column(name = "creado_por_usuario_id", nullable = false)
    private Long creadoPorUsuarioId;
    @Column(nullable = false, length = 150)
    private String nombre;
    @Column(nullable = false, columnDefinition = "text")
    private String descripcion;
    @Column(nullable = false, length = 150)
    private String destino;
    @Column(name = "duracion_horas")
    private Integer duracionHoras;
    @Column(columnDefinition = "text")
    private String recomendaciones;
    @Column(nullable = false)
    private boolean activo = true;
    @Column(name = "creado_en", nullable = false, updatable = false)
    private OffsetDateTime creadoEn;
    @Column(name = "actualizado_en")
    private OffsetDateTime actualizadoEn;

    /**
     * Inicializa la auditoria de una experiencia nueva.
     */
    @PrePersist
    void inicializarMarcaTemporal() {
        creadoEn = OffsetDateTime.now();
    }

    /**
     * Actualiza la marca temporal antes de persistir cambios.
     */
    @PreUpdate
    void actualizarMarcaTemporal() {
        actualizadoEn = OffsetDateTime.now();
    }
}
