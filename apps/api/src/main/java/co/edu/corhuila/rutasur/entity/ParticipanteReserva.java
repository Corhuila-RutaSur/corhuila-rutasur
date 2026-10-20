package co.edu.corhuila.rutasur.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Persona que asistira mediante una reserva, aunque no sea quien compra.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "participante_reserva")
public class ParticipanteReserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "reserva_id", nullable = false)
    private Long reservaId;
    @Column(name = "tipo_documento", length = 20)
    private String tipoDocumento;
    @Column(name = "numero_documento", length = 30)
    private String numeroDocumento;
    @Column(nullable = false, length = 100)
    private String nombres;
    @Column(nullable = false, length = 100)
    private String apellidos;
    @Column(length = 30)
    private String telefono;
    @Column(length = 150)
    private String correo;
    @Column(name = "es_responsable", nullable = false)
    private boolean responsable;
}
