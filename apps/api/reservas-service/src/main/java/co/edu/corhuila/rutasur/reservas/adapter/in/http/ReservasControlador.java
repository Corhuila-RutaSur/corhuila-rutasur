package co.edu.corhuila.rutasur.reservas.adapter.in.http;

import co.edu.corhuila.rutasur.reservas.application.port.in.*;
import co.edu.corhuila.rutasur.reservas.domain.model.Reserva;
import jakarta.validation.Valid;

import java.util.*;

import lombok.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoint que registra y cancela reservas sobre el dueño de cada reserva.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/reservas")
public class ReservasControlador {
    private final CrearReservaUseCase servicio;
    private final CancelarReservaUseCase cancelarServicio;
    private final ConsultarReservasUsuarioUseCase consultarReservas;

    @PostMapping
    public ResponseEntity<RespuestaGeneral<ReservaResponse>> crear(@Valid @RequestBody ReservaCreacionRequest r, @RequestHeader(value = "X-Correlation-Id", required = false) UUID c) {
        UUID correlacion = c == null ? UUID.randomUUID() : c;
        Reserva reserva = servicio.crear(new CrearReservaCommand(r.usuarioIdExterno(), r.salidaId(), r.cantidadParticipantes(), r.participantes().stream().map(p -> new ParticipanteReservaCommand(p.nombres(), p.apellidos(), p.tipoDocumento(), p.numeroDocumento())).toList(), r.programacionesServicio() == null ? List.of() : r.programacionesServicio(), r.programacionesActividad() == null ? List.of() : r.programacionesActividad(), correlacion));
        return ResponseEntity.status(HttpStatus.CREATED).body(RespuestaGeneral.exitosa("201", "reserva.creada", "Reserva creada correctamente.", new ReservaResponse(reserva.id(), reserva.codigo(), reserva.salidaId(), reserva.cantidadParticipantes(), reserva.total(), reserva.estado())));
    }

    /** Consulta el historial del usuario; la autorización por token se añadirá en el borde distribuido. */
    @GetMapping("/usuarios/{usuarioId}")
    public ResponseEntity<RespuestaGeneral<List<ReservaUsuarioResponse>>> listarPorUsuario(@PathVariable UUID usuarioId) {
        List<ReservaUsuarioResponse> respuesta = consultarReservas.consultar(usuarioId).stream()
                .map(item -> new ReservaUsuarioResponse(item.id(), item.codigo(), item.salidaId(), item.experienciaId(), item.experienciaNombre(), item.fechaHoraInicio(), item.cantidadParticipantes(), item.total(), item.estado(), item.creadaEn())).toList();
        return ResponseEntity.ok(RespuestaGeneral.exitosa("200", "reserva.usuario.consultada", "Reservas del usuario consultadas correctamente.", respuesta));
    }

    @DeleteMapping("/{reservaId}")
    public ResponseEntity<RespuestaGeneral<ReservaResponse>> cancelar(@PathVariable UUID reservaId, @Valid @RequestBody(required = false) CancelacionReservaRequest solicitud, @RequestHeader("X-Usuario-Id") UUID usuarioId, @RequestHeader(value = "X-Correlation-Id", required = false) UUID c) {
        UUID correlacion = c == null ? UUID.randomUUID() : c;
        String motivo = solicitud == null ? null : solicitud.motivo();
        Reserva reserva = cancelarServicio.cancelar(new CancelarReservaCommand(reservaId, usuarioId, motivo, correlacion));
        return ResponseEntity.ok(RespuestaGeneral.exitosa("200", "reserva.cancelada", "Reserva cancelada correctamente.", new ReservaResponse(reserva.id(), reserva.codigo(), reserva.salidaId(), reserva.cantidadParticipantes(), reserva.total(), reserva.estado())));
    }
}
