package co.edu.corhuila.rutasur.controller;

import co.edu.corhuila.rutasur.api.ReservaApi;
import co.edu.corhuila.rutasur.dto.request.ReservaActualizacionRequest;
import co.edu.corhuila.rutasur.dto.request.ReservaCreacionRequest;
import co.edu.corhuila.rutasur.dto.response.ReservaResponse;
import co.edu.corhuila.rutasur.service.ReservaServicio;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

/**
 * Adaptador REST que delega la lógica de reservas al servicio.
 */
@RestController
@RequiredArgsConstructor
public class ReservaController implements ReservaApi {
    private final ReservaServicio reservaServicio;

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasRole('ADMINISTRADOR') or #solicitud.usuarioId == authentication.principal.usuarioId")
    public ResponseEntity<ReservaResponse> crear(ReservaCreacionRequest solicitud) {
        ReservaResponse reserva = reservaServicio.crear(solicitud);
        return ResponseEntity.created(URI.create("/api/v1/reservas/" + reserva.id())).body(reserva);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasRole('ADMINISTRADOR') or @autorizacionReserva.esPropietario(#id, authentication.principal.usuarioId)")
    public ResponseEntity<ReservaResponse> consultarPorId(Long id) {
        return ResponseEntity.ok(reservaServicio.consultarPorId(id));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasRole('ADMINISTRADOR') or #usuarioId == authentication.principal.usuarioId")
    public ResponseEntity<List<ReservaResponse>> consultarPorUsuario(Long usuarioId) {
        return ResponseEntity.ok(reservaServicio.consultarPorUsuario(usuarioId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasRole('ADMINISTRADOR') or @autorizacionReserva.esPropietario(#id, authentication.principal.usuarioId)")
    public ResponseEntity<ReservaResponse> actualizar(Long id, ReservaActualizacionRequest solicitud) {
        return ResponseEntity.ok(reservaServicio.actualizar(id, solicitud));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasRole('ADMINISTRADOR') or @autorizacionReserva.esPropietario(#id, authentication.principal.usuarioId)")
    public ResponseEntity<Void> cancelar(Long id) {
        reservaServicio.cancelar(id);
        return ResponseEntity.noContent().build();
    }
}
