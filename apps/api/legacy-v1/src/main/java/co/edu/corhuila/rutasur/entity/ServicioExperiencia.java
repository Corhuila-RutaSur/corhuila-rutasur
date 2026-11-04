package co.edu.corhuila.rutasur.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Servicio que una experiencia puede ofrecer en sus salidas.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "servicio_experiencia")
public class ServicioExperiencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "experiencia_id", nullable = false)
    private Long experienciaId;
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, columnDefinition = "tipo_servicio")
    private TipoServicio tipo;
    @Column(nullable = false, length = 150)
    private String nombre;
    @Column(columnDefinition = "text")
    private String descripcion;
    @Column(nullable = false)
    private boolean activo = true;
}
