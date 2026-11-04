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
 * Contrato de administración de actividades pertenecientes a una experiencia.
 */
@RequestMapping("/api/v2/administracion/experiencias/{experienciaId}/actividades")
public interface AdministracionActividadesApi {
    @PostMapping
    ResponseEntity<RespuestaGeneral<ActividadResponse>> crear(@PathVariable UUID experienciaId, @Valid @RequestBody ActividadRequest solicitud, @RequestHeader(value = "X-Correlation-Id", required = false) UUID correlacionId);

    @GetMapping
    ResponseEntity<RespuestaGeneral<List<ActividadResponse>>> consultarActivas(@PathVariable UUID experienciaId);

    @PutMapping("/{actividadId}")
    ResponseEntity<RespuestaGeneral<ActividadResponse>> actualizar(@PathVariable UUID experienciaId, @PathVariable UUID actividadId, @Valid @RequestBody ActividadRequest solicitud, @RequestHeader(value = "X-Correlation-Id", required = false) UUID correlacionId);

    @DeleteMapping("/{actividadId}")
    ResponseEntity<RespuestaGeneral<Void>> inactivar(@PathVariable UUID experienciaId, @PathVariable UUID actividadId);
}
