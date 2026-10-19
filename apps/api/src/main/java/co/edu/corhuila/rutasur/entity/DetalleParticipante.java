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

/** Asigna un participante a un servicio comprado. */
@Getter @Setter @NoArgsConstructor @Entity @Table(name = "detalle_participante")
public class DetalleParticipante {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "detalle_reserva_id", nullable = false) private Long detalleReservaId;
    @Column(name = "participante_reserva_id", nullable = false) private Long participanteReservaId;
}
