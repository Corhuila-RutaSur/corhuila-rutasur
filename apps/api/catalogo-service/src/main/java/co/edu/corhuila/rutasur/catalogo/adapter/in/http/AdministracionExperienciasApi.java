package co.edu.corhuila.rutasur.catalogo.adapter.in.http;

import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Contrato administrativo para creación de experiencias por Gateway autorizado.
 */
@RequestMapping("/api/v2/administracion/experiencias")
public interface AdministracionExperienciasApi {
    @PostMapping
    ResponseEntity<RespuestaGeneral<ExperienciaResponse>> crear(@Valid @RequestBody ExperienciaCreacionRequest solicitud,
                                                                @RequestHeader(value = "X-Correlation-Id", required = false) UUID correlacionId);

    @GetMapping
    ResponseEntity<RespuestaGeneral<List<ExperienciaResponse>>> consultarActivas();

    @GetMapping("/{identificador}")
    ResponseEntity<RespuestaGeneral<ExperienciaResponse>> consultarPorId(@PathVariable UUID identificador);

    @PutMapping("/{identificador}")
    ResponseEntity<RespuestaGeneral<ExperienciaResponse>> actualizar(@PathVariable UUID identificador, @Valid @RequestBody ExperienciaCreacionRequest solicitud, @RequestHeader(value = "X-Correlation-Id", required = false) UUID correlacionId);

    @DeleteMapping("/{identificador}")
    ResponseEntity<RespuestaGeneral<Void>> inactivar(@PathVariable UUID identificador);
}
