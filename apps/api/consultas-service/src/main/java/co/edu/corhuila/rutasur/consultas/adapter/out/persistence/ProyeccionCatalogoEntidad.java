package co.edu.corhuila.rutasur.consultas.adapter.out.persistence;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "experiencia_proyeccion")
class ExperienciaProyeccionEntidad {
    @Id
    @Column(name = "experiencia_id")
    UUID id;
    String nombre;
    String destino;
    String descripcion;
    @Column(name = "imagen_principal")
    String imagenPrincipal;
    @Column(name = "calificacion_promedio")
    java.math.BigDecimal calificacionPromedio;
    boolean activa;
    @Column(name = "actualizado_en")
    OffsetDateTime actualizadoEn;
}

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "servicio_proyeccion")
class ServicioProyeccionEntidad {
    @Id
    @Column(name = "servicio_id")
    UUID id;
    @Column(name = "experiencia_id")
    UUID experienciaId;
    String tipo;
    String nombre;
    String descripcion;
    boolean activo;
    @Column(name = "actualizado_en")
    OffsetDateTime actualizadoEn;
}

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "actividad_proyeccion")
class ActividadProyeccionEntidad {
    @Id
    @Column(name = "actividad_id")
    UUID id;
    @Column(name = "experiencia_id")
    UUID experienciaId;
    String nombre;
    String descripcion;
    @Column(name = "duracion_horas")
    int duracionHoras;
    boolean activa;
    @Column(name = "actualizado_en")
    OffsetDateTime actualizadoEn;
}

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "inbox_evento")
class InboxEntidad {
    @Id
    @Column(name = "id_evento")
    UUID id;
    String tipo;
    String origen;
    @Column(name = "recibido_en")
    OffsetDateTime recibidoEn;
    @Column(name = "procesado_en")
    OffsetDateTime procesadoEn;
    String estado;
}

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "salida_proyeccion")
class SalidaProyeccionEntidad {
    @Id
    @Column(name = "salida_id")
    UUID id;
    @Column(name = "experiencia_id")
    UUID experienciaId;
    @Column(name = "fecha_hora_inicio")
    OffsetDateTime fechaHoraInicio;
    @Column(name = "cupos_disponibles")
    int cuposDisponibles;
    String estado;
    @Column(name = "actualizado_en")
    OffsetDateTime actualizadoEn;
}

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "imagen_experiencia_proyeccion")
class ImagenExperienciaProyeccionEntidad {
    @Id @Column(name = "imagen_id") UUID id;
    @Column(name = "experiencia_id") UUID experienciaId;
    String ruta;
    boolean principal;
    int orden;
    boolean activa;
    @Column(name = "actualizado_en") OffsetDateTime actualizadoEn;
}

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "programacion_servicio_proyeccion")
class ProgramacionServicioProyeccionEntidad {
    @Id @Column(name = "programacion_id") UUID id;
    @Column(name = "salida_id") UUID salidaId;
    @Column(name = "servicio_id_externo") UUID servicioIdExterno;
    String nombre;
    java.math.BigDecimal precio;
    @Column(name = "cupos_disponibles") int cuposDisponibles;
    boolean activo;
    @Column(name = "actualizado_en") OffsetDateTime actualizadoEn;
}

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "programacion_actividad_proyeccion")
class ProgramacionActividadProyeccionEntidad {
    @Id @Column(name = "programacion_id") UUID id;
    @Column(name = "salida_id") UUID salidaId;
    @Column(name = "actividad_id_externa") UUID actividadIdExterna;
    String nombre;
    java.math.BigDecimal precio;
    boolean activo;
    @Column(name = "actualizado_en") OffsetDateTime actualizadoEn;
}
