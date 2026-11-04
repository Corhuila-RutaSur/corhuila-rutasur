package co.edu.corhuila.rutasur.pagos.adapter.out.persistence;

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
 * Evidencia local de un evento externo tratado por Pagos.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "inbox_evento")
class EventoInboxEntidad {
    @Id
    @Column(name = "id_evento")
    private UUID idEvento;
    private String tipo;
    private String origen;
    @Column(name = "recibido_en")
    private OffsetDateTime recibidoEn;
    @Column(name = "procesado_en")
    private OffsetDateTime procesadoEn;
    private String estado;
}
