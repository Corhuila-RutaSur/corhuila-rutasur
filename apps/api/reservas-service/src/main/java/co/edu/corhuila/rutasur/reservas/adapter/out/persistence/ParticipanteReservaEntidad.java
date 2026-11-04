package co.edu.corhuila.rutasur.reservas.adapter.out.persistence;

import jakarta.persistence.*;

import java.util.*;

import lombok.*;

/**
 * Entidad JPA privada de participantes.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "participante_reserva")
class ParticipanteReservaEntidad {
    @Id
    private UUID id;
    @Column(name = "reserva_id")
    private UUID reservaId;
    private String nombres;
    private String apellidos;
    @Column(name = "tipo_documento")
    private String tipoDocumento;
    @Column(name = "numero_documento")
    private String numeroDocumento;
}
