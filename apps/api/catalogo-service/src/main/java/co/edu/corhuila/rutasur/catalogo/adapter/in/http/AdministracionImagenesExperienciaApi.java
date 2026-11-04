package co.edu.corhuila.rutasur.catalogo.adapter.in.http;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

/** Contrato OpenAPI de administración de la galería de experiencias. */
@RequestMapping("/api/v2/administracion/experiencias/{experienciaId}/imagenes")
public interface AdministracionImagenesExperienciaApi {
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<RespuestaGeneral<ImagenExperienciaResponse>> cargar(@PathVariable UUID experienciaId,
        @RequestPart("archivo") MultipartFile archivo, @RequestParam(defaultValue = "false") boolean principal,
        @RequestParam(defaultValue = "0") int orden, @RequestHeader(value = "X-Correlation-Id", required = false) UUID correlacionId);
    @GetMapping ResponseEntity<RespuestaGeneral<List<ImagenExperienciaResponse>>> listar(@PathVariable UUID experienciaId);
    @PutMapping("/{imagenId}") ResponseEntity<RespuestaGeneral<ImagenExperienciaResponse>> actualizar(@PathVariable UUID experienciaId,
        @PathVariable UUID imagenId, @Valid @RequestBody ImagenExperienciaActualizacionRequest solicitud,
        @RequestHeader(value = "X-Correlation-Id", required = false) UUID correlacionId);
    @DeleteMapping("/{imagenId}") ResponseEntity<RespuestaGeneral<Void>> inactivar(@PathVariable UUID experienciaId,
        @PathVariable UUID imagenId, @RequestHeader(value = "X-Correlation-Id", required = false) UUID correlacionId);
}
