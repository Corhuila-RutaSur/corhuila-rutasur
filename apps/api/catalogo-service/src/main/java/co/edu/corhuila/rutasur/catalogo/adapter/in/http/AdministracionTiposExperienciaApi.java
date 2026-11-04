package co.edu.corhuila.rutasur.catalogo.adapter.in.http;

import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Contrato HTTP administrativo del catálogo de tipos de experiencia.
 */
@RequestMapping("/api/v2/administracion/tipos-experiencia")
public interface AdministracionTiposExperienciaApi {
    @PostMapping
    ResponseEntity<RespuestaGeneral<TipoExperienciaResponse>> crear(@Valid @RequestBody TipoExperienciaRequest solicitud, @RequestHeader(value = "X-Correlation-Id", required = false) UUID correlacionId);

    @PutMapping("/{identificador}")
    ResponseEntity<RespuestaGeneral<TipoExperienciaResponse>> actualizar(@PathVariable UUID identificador, @Valid @RequestBody TipoExperienciaRequest solicitud, @RequestHeader(value = "X-Correlation-Id", required = false) UUID correlacionId);

    @GetMapping
    ResponseEntity<RespuestaGeneral<List<TipoExperienciaResponse>>> consultarActivos();

    @DeleteMapping("/{identificador}")
    ResponseEntity<RespuestaGeneral<Void>> inactivar(@PathVariable UUID identificador);
}
