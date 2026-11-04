package co.edu.corhuila.rutasur.api;

import co.edu.corhuila.rutasur.dto.request.ExperienciaActualizacionRequest;
import co.edu.corhuila.rutasur.dto.request.ExperienciaCreacionRequest;
import co.edu.corhuila.rutasur.dto.response.ExperienciaResponse;
import co.edu.corhuila.rutasur.dto.response.ExperienciaImagenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Contrato HTTP del catálogo de experiencias.
 */
@Tag(name = "Experiencias", description = "Administración del catálogo turístico")
public interface ExperienciaApi {
    @Operation(summary = "Crear experiencia", description = "Requiere rol ADMINISTRADOR.")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/api/v1/experiencias")
    ResponseEntity<ExperienciaResponse> crear(@Valid @RequestBody ExperienciaCreacionRequest solicitud);

    @Operation(summary = "Consultar experiencia")
    @GetMapping("/api/v1/public/experiencias/{id}")
    ResponseEntity<ExperienciaResponse> consultarPorId(@PathVariable Long id);

    @Operation(summary = "Listar experiencias")
    @GetMapping("/api/v1/public/experiencias")
    ResponseEntity<List<ExperienciaResponse>> consultarActivas();

    @Operation(summary = "Actualizar experiencia")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/api/v1/experiencias/{id}")
    ResponseEntity<ExperienciaResponse> actualizar(@PathVariable Long id, @Valid @RequestBody ExperienciaActualizacionRequest solicitud);

    @Operation(summary = "Inactivar experiencia")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/api/v1/experiencias/{id}")
    ResponseEntity<Void> eliminarLogicamente(@PathVariable Long id);

    @Operation(summary = "Adjuntar imagen de experiencia")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping(value = "/api/v1/experiencias/{id}/imagenes", consumes = "multipart/form-data")
    ResponseEntity<ExperienciaImagenResponse> adjuntarImagen(@PathVariable Long id, @RequestPart("archivo") MultipartFile archivo, @RequestParam(defaultValue = "false") boolean principal, @RequestParam(defaultValue = "1") int orden);
}
