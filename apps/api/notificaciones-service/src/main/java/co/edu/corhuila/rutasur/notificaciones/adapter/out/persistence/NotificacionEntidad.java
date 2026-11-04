package co.edu.corhuila.rutasur.notificaciones.adapter.out.persistence;

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
 * Entidad JPA privada del buzón de notificaciones internas.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "notificacion")
class NotificacionEntidad {
    @Id
    private UUID id;
    @Column(name = "evento_id")
    private UUID eventoId;
    @Column(name = "destinatario")
    private String destinatario;
    private String canal;
    private String asunto;
    @Column(name = "contenido")
    private String mensaje;
    @Column(name = "estado")
    private String estado;
    @Column(name = "creada_en")
    private OffsetDateTime creadaEn;
    @Column(name = "enviada_en")
    private OffsetDateTime leidaEn;
}
