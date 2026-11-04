package co.edu.corhuila.rutasur.reservas.adapter.out.persistence;

import co.edu.corhuila.rutasur.reservas.application.port.out.ReservaPuertoSalida;
import co.edu.corhuila.rutasur.reservas.domain.model.Reserva;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lombok.*;
import org.springframework.stereotype.Component;

/**
 * Adaptador JPA de reservas.
 */
@Component
@RequiredArgsConstructor
public class ReservaPersistenciaAdaptador implements ReservaPuertoSalida {
    private final ReservaRepositorioJpa repositorio;

    @Override
    public Reserva guardar(Reserva r) {
        UUID id = r.id() == null ? UUID.randomUUID() : r.id();
        ReservaEntidad e = repositorio.findById(id).orElseGet(ReservaEntidad::new);
        e.setId(id);
        e.setCodigo(r.codigo());
        e.setUsuarioIdExterno(r.usuarioIdExterno());
        e.setSalidaId(r.salidaId());
        e.setCantidadParticipantes(r.cantidadParticipantes());
        e.setTotal(r.total());
        e.setEstado(r.estado());
        e.setCorrelacionId(r.correlacionId());
        e.setCreadaEn(r.creadaEn());
        e = repositorio.save(e);
        return convertir(e);
    }

    @Override
    public Optional<Reserva> buscarPorId(UUID reservaId) {
        return repositorio.findById(reservaId).map(this::convertir);
    }

    @Override
    public List<Reserva> consultarPorUsuario(UUID usuarioIdExterno) {
        return repositorio.findAllByUsuarioIdExternoOrderByCreadaEnDesc(usuarioIdExterno).stream().map(this::convertir).toList();
    }

    private Reserva convertir(ReservaEntidad e) {
        return new Reserva(e.getId(), e.getCodigo(), e.getUsuarioIdExterno(), e.getSalidaId(), e.getCantidadParticipantes(), e.getTotal(), e.getEstado(), e.getCorrelacionId(), e.getCreadaEn());
    }
}
