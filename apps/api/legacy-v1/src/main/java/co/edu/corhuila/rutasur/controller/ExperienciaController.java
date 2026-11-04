package co.edu.corhuila.rutasur.controller;

import co.edu.corhuila.rutasur.api.ExperienciaApi;
import co.edu.corhuila.rutasur.dto.request.ExperienciaActualizacionRequest;
import co.edu.corhuila.rutasur.dto.request.ExperienciaCreacionRequest;
import co.edu.corhuila.rutasur.dto.response.ExperienciaResponse;
import co.edu.corhuila.rutasur.dto.response.ExperienciaImagenResponse;
import co.edu.corhuila.rutasur.service.ExperienciaServicio;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;

/**
 * Adaptador REST de experiencias.
 */
@RestController
@RequiredArgsConstructor
public class ExperienciaController implements ExperienciaApi {
    private final ExperienciaServicio experienciaServicio;

    @Override
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ExperienciaResponse> crear(ExperienciaCreacionRequest solicitud) {
        ExperienciaResponse experiencia = experienciaServicio.crear(solicitud);
        return ResponseEntity.created(URI.create("/api/v1/experiencias/" + experiencia.id())).body(experiencia);
    }

    @Override
    public ResponseEntity<ExperienciaResponse> consultarPorId(Long id) {
        return ResponseEntity.ok(experienciaServicio.consultarPorId(id));
    }

    @Override
    public ResponseEntity<List<ExperienciaResponse>> consultarActivas() {
        return ResponseEntity.ok(experienciaServicio.consultarActivas());
    }

    @Override
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ExperienciaResponse> actualizar(Long id, ExperienciaActualizacionRequest solicitud) {
        return ResponseEntity.ok(experienciaServicio.actualizar(id, solicitud));
    }

    @Override
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> eliminarLogicamente(Long id) {
        experienciaServicio.eliminarLogicamente(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ExperienciaImagenResponse> adjuntarImagen(Long id, MultipartFile archivo, boolean principal, int orden) {
        return ResponseEntity.ok(experienciaServicio.adjuntarImagen(id, archivo, principal, orden));
    }
}
