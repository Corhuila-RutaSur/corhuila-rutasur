package co.edu.corhuila.rutasur.reservas.adapter.in.http;

import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Contrato HTTP para creación y consulta de salidas.
 */
@RequestMapping("/api/v2/administracion/salidas")
public interface AdministracionSalidasApi {
    @PostMapping
    ResponseEntity<RespuestaGeneral<SalidaResponse>> crear(@Valid @RequestBody SalidaRequest solicitud, @RequestHeader(value = "X-Correlation-Id", required = false) UUID correlacionId);

    @GetMapping
    ResponseEntity<RespuestaGeneral<List<SalidaResponse>>> consultarDisponibles();
}
