package co.edu.corhuila.rutasur.notificaciones.adapter.out.persistence;

import co.edu.corhuila.rutasur.notificaciones.application.port.out.NotificacionPuertoSalida;
import co.edu.corhuila.rutasur.notificaciones.domain.model.Notificacion;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Adaptador JPA del buzón interno.
 */
@Component
@RequiredArgsConstructor
public class NotificacionPersistenciaAdaptador implements NotificacionPuertoSalida {
    private final NotificacionRepositorioJpa repositorio;

    @Override
    public Notificacion guardar(Notificacion notificacion) {
        UUID id = notificacion.id() == null ? UUID.randomUUID() : notificacion.id();
        NotificacionEntidad entidad = repositorio.findById(id).orElseGet(NotificacionEntidad::new);
        entidad.setId(id);
        entidad.setEventoId(notificacion.eventoId());
        entidad.setDestinatario(notificacion.usuarioIdExterno().toString());
        entidad.setCanal("INTERNA");
        entidad.setAsunto(notificacion.titulo());
        entidad.setMensaje(notificacion.mensaje());
        entidad.setEstado(notificacion.estado());
        entidad.setCreadaEn(notificacion.creadaEn());
        entidad.setLeidaEn(notificacion.leidaEn());
        return aDominio(repositorio.save(entidad));
    }

    @Override
    public List<Notificacion> consultarPorUsuario(UUID usuarioId) {
        return repositorio.findAllByDestinatarioOrderByCreadaEnDesc(usuarioId.toString()).stream().map(this::aDominio).toList();
    }

    @Override
    public Optional<Notificacion> buscarPorIdYUsuario(UUID notificacionId, UUID usuarioId) {
        return repositorio.findByIdAndDestinatario(notificacionId, usuarioId.toString()).map(this::aDominio);
    }

    private Notificacion aDominio(NotificacionEntidad entidad) {
        return new Notificacion(entidad.getId(), entidad.getEventoId(), UUID.fromString(entidad.getDestinatario()), entidad.getCanal(), entidad.getAsunto(), entidad.getMensaje(), entidad.getEstado(), entidad.getCreadaEn(), entidad.getLeidaEn());
    }
}
