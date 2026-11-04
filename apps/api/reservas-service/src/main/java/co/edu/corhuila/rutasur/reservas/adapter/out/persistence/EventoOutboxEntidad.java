package co.edu.corhuila.rutasur.reservas.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Registro Outbox publicado solo después de confirmar la transacción de Reservas.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "outbox_evento")
class EventoOutboxEntidad {
    @Id
    private UUID id;
    private String tipo;
    private int version;
    @Column(name = "agregado_id")
    private UUID agregadoId;
    @Column(name = "correlacion_id")
    private UUID correlacionId;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String datos;
    @Column(name = "ocurrido_en")
    private OffsetDateTime ocurridoEn;
    @Column(name = "publicado_en")
    private OffsetDateTime publicadoEn;
    @Column(name = "intentos_publicacion")
    private int intentosPublicacion;
    @Column(name = "ultimo_error")
    private String ultimoError;
}
