package co.edu.corhuila.rutasur.pagos.adapter.in.http;

import co.edu.corhuila.rutasur.pagos.application.port.in.ProcesarPagoCommand;
import co.edu.corhuila.rutasur.pagos.application.port.in.ProcesarPagoUseCase;
import co.edu.corhuila.rutasur.pagos.application.port.in.ResultadoPagoSimulado;
import co.edu.corhuila.rutasur.pagos.domain.model.Pago;
import jakarta.validation.Valid;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint explícito de cobro simulado.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/pagos")
public class PagosControlador {
    private final ProcesarPagoUseCase servicio;

    /**
     * Procesa un pago simulado o devuelve el resultado de un reintento idempotente.
     */
    @PostMapping
    public ResponseEntity<RespuestaGeneral<PagoResponse>> pagar(@Valid @RequestBody PagoRequest solicitud,
                                                                @RequestHeader("X-Correlation-Id") UUID correlacionId) {
        ResultadoPagoSimulado resultadoSolicitado = solicitud.resultado() == null ? ResultadoPagoSimulado.APROBADO : solicitud.resultado();
        Pago pago = servicio.procesar(new ProcesarPagoCommand(solicitud.reservaIdExterna(), solicitud.monto(), resultadoSolicitado, correlacionId));
        PagoResponse respuesta = new PagoResponse(pago.id(), pago.reservaIdExterna(), pago.monto(), pago.moneda(), pago.estado(), pago.referenciaProveedor());
        String mensaje = "APROBADO".equals(pago.estado()) ? "Pago simulado aprobado correctamente." : "El pago simulado terminó en estado " + pago.estado() + ".";
        return ResponseEntity.status(HttpStatus.CREATED).body(RespuestaGeneral.exitosa("201", "pago." + pago.estado().toLowerCase(), mensaje, respuesta));
    }
}
