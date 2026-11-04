package co.edu.corhuila.rutasur.reservas.adapter.in.http;

import co.edu.corhuila.rutasur.reservas.application.port.in.GestionarSalidaUseCase;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Adaptador REST de salidas administrativas.
 */
@RestController
@RequiredArgsConstructor
public class AdministracionSalidasControlador implements AdministracionSalidasApi {
    private final GestionarSalidaUseCase gestionarSalidas;
    private final SalidaHttpMapper mapper;
    private final MessageSource mensajes;

    @Override
    public ResponseEntity<RespuestaGeneral<SalidaResponse>> crear(SalidaRequest solicitud, UUID correlacionId) {
        UUID correlacion = correlacionId == null ? UUID.randomUUID() : correlacionId;
        return ResponseEntity.status(HttpStatus.CREATED).header("X-Correlation-Id", correlacion.toString()).body(RespuestaGeneral.exitosa("201", "salida.creada", mensaje("salida.creada"), mapper.aRespuesta(gestionarSalidas.crear(mapper.aComando(solicitud, correlacion)))));
    }

    @Override
    public ResponseEntity<RespuestaGeneral<List<SalidaResponse>>> consultarDisponibles() {
        return ResponseEntity.ok(RespuestaGeneral.exitosa("200", "salida.consultada", mensaje("salida.consultada"), gestionarSalidas.consultarDisponibles().stream().map(mapper::aRespuesta).toList()));
    }

    private String mensaje(String codigo) {
        return mensajes.getMessage(codigo, null, LocaleContextHolder.getLocale());
    }
}
