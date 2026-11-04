package co.edu.corhuila.rutasur.reservas.adapter.out.persistence;

import co.edu.corhuila.rutasur.reservas.application.port.out.ParticipanteReservaPuertoSalida;
import co.edu.corhuila.rutasur.reservas.domain.model.ParticipanteReserva;

import java.util.*;

import lombok.*;
import org.springframework.stereotype.Component;

/**
 * Adaptador JPA de participantes de una reserva.
 */
@Component
@RequiredArgsConstructor
public class ParticipanteReservaPersistenciaAdaptador implements ParticipanteReservaPuertoSalida {
    private final ParticipanteReservaRepositorioJpa repositorio;

    @Override
    public void guardarTodos(List<ParticipanteReserva> valores) {
        repositorio.saveAll(valores.stream().map(p -> {
            ParticipanteReservaEntidad e = new ParticipanteReservaEntidad();
            e.setId(UUID.randomUUID());
            e.setReservaId(p.reservaId());
            e.setNombres(p.nombres());
            e.setApellidos(p.apellidos());
            e.setTipoDocumento(p.tipoDocumento());
            e.setNumeroDocumento(p.numeroDocumento());
            return e;
        }).toList());
    }
}
