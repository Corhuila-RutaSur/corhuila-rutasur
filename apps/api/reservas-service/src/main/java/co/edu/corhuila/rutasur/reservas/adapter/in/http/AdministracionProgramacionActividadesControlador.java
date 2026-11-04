package co.edu.corhuila.rutasur.reservas.adapter.in.http;

import co.edu.corhuila.rutasur.reservas.application.port.in.ProgramarActividadCommand;
import co.edu.corhuila.rutasur.reservas.application.usecase.ProgramarActividadSalidaServicio;
import co.edu.corhuila.rutasur.reservas.domain.model.ProgramacionActividad;
import jakarta.validation.Valid;

import java.util.*;

import lombok.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoint administrativo que habilita actividades para una salida.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/administracion/salidas/{salidaId}/actividades")
public class AdministracionProgramacionActividadesControlador {
    private final ProgramarActividadSalidaServicio servicio;

    @PostMapping
    public ResponseEntity<RespuestaGeneral<ProgramacionActividadResponse>> crear(@PathVariable UUID salidaId, @Valid @RequestBody ProgramacionActividadRequest r, @RequestHeader(value = "X-Correlation-Id", required = false) UUID c) {
        UUID correlacion = c == null ? UUID.randomUUID() : c;
        ProgramacionActividad p = servicio.ejecutar(salidaId, new ProgramarActividadCommand(r.actividadIdExterna(), r.nombre(), r.precio(), correlacion));
        return ResponseEntity.status(HttpStatus.CREATED).body(RespuestaGeneral.exitosa("201", "programacion.actividad.creada", "Actividad habilitada para la salida.", new ProgramacionActividadResponse(p.id(), p.salidaId(), p.actividadIdExterna(), p.nombre(), p.precio(), p.activo())));
    }
}
