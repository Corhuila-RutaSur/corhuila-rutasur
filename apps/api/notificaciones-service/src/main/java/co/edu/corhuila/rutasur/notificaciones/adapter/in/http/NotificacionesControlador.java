package co.edu.corhuila.rutasur.notificaciones.adapter.in.http;

import co.edu.corhuila.rutasur.notificaciones.application.port.in.ConsultarNotificacionesUseCase;
import co.edu.corhuila.rutasur.notificaciones.application.port.in.MarcarNotificacionLeidaUseCase;
import co.edu.corhuila.rutasur.notificaciones.domain.model.Notificacion;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * API del buzón de notificaciones internas. La identidad se integrará desde el Gateway/JWT.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/usuarios/{usuarioId}/notificaciones")
public class NotificacionesControlador {
    private final ConsultarNotificacionesUseCase consultar;
    private final MarcarNotificacionLeidaUseCase marcarLeida;

    @GetMapping
    public ResponseEntity<RespuestaGeneral<List<NotificacionResponse>>> listar(@PathVariable UUID usuarioId) {
        List<NotificacionResponse> respuesta = consultar.consultar(usuarioId).stream().map(this::aResponse).toList();
        return ResponseEntity.ok(RespuestaGeneral.exitosa("200", "notificaciones.consultadas", "Notificaciones consultadas correctamente.", respuesta));
    }

    @PatchMapping("/{notificacionId}/leida")
    public ResponseEntity<RespuestaGeneral<NotificacionResponse>> leer(@PathVariable UUID usuarioId, @PathVariable UUID notificacionId) {
        return ResponseEntity.ok(RespuestaGeneral.exitosa("200", "notificacion.leida", "Notificación marcada como leída.", aResponse(marcarLeida.marcarLeida(usuarioId, notificacionId))));
    }

    private NotificacionResponse aResponse(Notificacion item) {
        return new NotificacionResponse(item.id(), item.tipo(), item.titulo(), item.mensaje(), item.estado(), item.creadaEn(), item.leidaEn());
    }
}
