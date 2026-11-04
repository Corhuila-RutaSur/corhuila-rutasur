package co.edu.corhuila.rutasur.reservas.adapter.out.persistence;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.*;

/**
 * Copia local de una referencia comercial de Catálogo.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "referencia_experiencia")
class ReferenciaExperienciaEntidad {
    @Id
    @Column(name = "id_externo")
    UUID id;
    String nombre;
    String destino;
    boolean activa;
    @Column(name = "actualizado_en")
    OffsetDateTime actualizadoEn;
}

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "referencia_servicio")
class ReferenciaServicioEntidad {
    @Id
    @Column(name = "id_externo")
    UUID id;
    @Column(name = "experiencia_id_externa")
    UUID experienciaId;
    String tipo;
    String nombre;
    boolean activo;
    @Column(name = "actualizado_en")
    OffsetDateTime actualizadoEn;
}

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "referencia_actividad")
class ReferenciaActividadEntidad {
    @Id
    @Column(name = "id_externo")
    UUID id;
    @Column(name = "experiencia_id_externa")
    UUID experienciaId;
    String nombre;
    boolean activo;
    @Column(name = "actualizado_en")
    OffsetDateTime actualizadoEn;
}
