package co.edu.corhuila.rutasur.reservas.adapter.in.http;

import co.edu.corhuila.rutasur.reservas.application.usecase.ProgramarServicioSalidaServicio;
import jakarta.validation.Valid;

import java.util.*;

import lombok.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoint administrativo que habilita servicios para una salida.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/administracion/salidas/{salidaId}/servicios")
public class AdministracionProgramacionServiciosControlador {
    private final ProgramarServicioSalidaServicio servicio;
    private final ProgramacionServicioHttpMapper mapper;

    @PostMapping
    public ResponseEntity<RespuestaGeneral<ProgramacionServicioResponse>> crear(@PathVariable UUID salidaId, @Valid @RequestBody ProgramacionServicioRequest r, @RequestHeader(value = "X-Correlation-Id", required = false) UUID c) {
        UUID correlacion = c == null ? UUID.randomUUID() : c;
        return ResponseEntity.status(HttpStatus.CREATED).body(RespuestaGeneral.exitosa("201", "programacion.servicio.creada", "Servicio habilitado para la salida.", mapper.aRespuesta(servicio.ejecutar(salidaId, mapper.aComando(r, correlacion)))));
    }
}
