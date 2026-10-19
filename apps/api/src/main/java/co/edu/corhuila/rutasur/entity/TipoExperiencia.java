package co.edu.corhuila.rutasur.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Clasificación turística disponible para una experiencia. */
@Getter @NoArgsConstructor @Entity @Table(name = "tipo_experiencia")
public class TipoExperiencia {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 80) private String nombre;
    @Column(nullable = false) private boolean activo;
}
