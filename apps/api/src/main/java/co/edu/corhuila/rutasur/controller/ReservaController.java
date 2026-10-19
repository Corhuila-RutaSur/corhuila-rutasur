package co.edu.corhuila.rutasur.controller;

import co.edu.corhuila.rutasur.api.ReservaApi;
import co.edu.corhuila.rutasur.dto.request.ReservaActualizacionRequest;
import co.edu.corhuila.rutasur.dto.request.ReservaCreacionRequest;
import co.edu.corhuila.rutasur.dto.response.ReservaResponse;
import co.edu.corhuila.rutasur.service.ReservaServicio;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/** Adaptador REST que delega la lógica de reservas al servicio. */
@RestController
public class ReservaController implements ReservaApi {
    private final ReservaServicio reservaServicio;
    /** Construye el controlador con el caso de uso de reservas. */
    public ReservaController(ReservaServicio reservaServicio) { this.reservaServicio = reservaServicio; }
    /** {@inheritDoc} */ @Override
    public ResponseEntity<ReservaResponse> crear(ReservaCreacionRequest solicitud) {
        ReservaResponse reserva = reservaServicio.crear(solicitud);
        return ResponseEntity.created(URI.create("/api/v1/reservas/" + reserva.id())).body(reserva);
    }
    /** {@inheritDoc} */ @Override
    public ResponseEntity<ReservaResponse> consultarPorId(Long id) { return ResponseEntity.ok(reservaServicio.consultarPorId(id)); }
    /** {@inheritDoc} */ @Override
    public ResponseEntity<List<ReservaResponse>> consultarPorUsuario(Long usuarioId) { return ResponseEntity.ok(reservaServicio.consultarPorUsuario(usuarioId)); }
    /** {@inheritDoc} */ @Override
    public ResponseEntity<ReservaResponse> actualizar(Long id, ReservaActualizacionRequest solicitud) { return ResponseEntity.ok(reservaServicio.actualizar(id, solicitud)); }
    /** {@inheritDoc} */ @Override
    public ResponseEntity<Void> cancelar(Long id) { reservaServicio.cancelar(id); return ResponseEntity.noContent().build(); }
}
