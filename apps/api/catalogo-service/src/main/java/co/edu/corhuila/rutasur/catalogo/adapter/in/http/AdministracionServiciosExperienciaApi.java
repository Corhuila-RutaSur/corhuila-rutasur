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
 * Contrato administrativo de servicios que pertenecen a una experiencia.
 */
@RequestMapping("/api/v2/administracion/experiencias/{experienciaId}/servicios")
public interface AdministracionServiciosExperienciaApi {
    @PostMapping
    ResponseEntity<RespuestaGeneral<ServicioExperienciaResponse>> crear(@PathVariable UUID experienciaId, @Valid @RequestBody ServicioExperienciaRequest solicitud, @RequestHeader(value = "X-Correlation-Id", required = false) UUID correlacionId);

    @GetMapping
    ResponseEntity<RespuestaGeneral<List<ServicioExperienciaResponse>>> consultarActivos(@PathVariable UUID experienciaId);

    @PutMapping("/{servicioId}")
    ResponseEntity<RespuestaGeneral<ServicioExperienciaResponse>> actualizar(@PathVariable UUID experienciaId, @PathVariable UUID servicioId, @Valid @RequestBody ServicioExperienciaRequest solicitud, @RequestHeader(value = "X-Correlation-Id", required = false) UUID correlacionId);

    @DeleteMapping("/{servicioId}")
    ResponseEntity<RespuestaGeneral<Void>> inactivar(@PathVariable UUID experienciaId, @PathVariable UUID servicioId);
}
