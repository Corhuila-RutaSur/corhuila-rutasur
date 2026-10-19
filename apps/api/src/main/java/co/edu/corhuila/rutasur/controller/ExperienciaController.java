package co.edu.corhuila.rutasur.controller;
import co.edu.corhuila.rutasur.api.ExperienciaApi;
import co.edu.corhuila.rutasur.dto.request.ExperienciaActualizacionRequest;
import co.edu.corhuila.rutasur.dto.request.ExperienciaCreacionRequest;
import co.edu.corhuila.rutasur.dto.response.ExperienciaResponse;
import co.edu.corhuila.rutasur.service.ExperienciaServicio;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
/** Adaptador REST de experiencias. */
@RestController public class ExperienciaController implements ExperienciaApi {
    private final ExperienciaServicio experienciaServicio;
    public ExperienciaController(ExperienciaServicio experienciaServicio) { this.experienciaServicio = experienciaServicio; }
    @Override public ResponseEntity<ExperienciaResponse> crear(ExperienciaCreacionRequest solicitud) { ExperienciaResponse experiencia = experienciaServicio.crear(solicitud); return ResponseEntity.created(URI.create("/api/v1/experiencias/" + experiencia.id())).body(experiencia); }
    @Override public ResponseEntity<ExperienciaResponse> consultarPorId(Long id) { return ResponseEntity.ok(experienciaServicio.consultarPorId(id)); }
    @Override public ResponseEntity<List<ExperienciaResponse>> consultarActivas() { return ResponseEntity.ok(experienciaServicio.consultarActivas()); }
    @Override public ResponseEntity<ExperienciaResponse> actualizar(Long id, ExperienciaActualizacionRequest solicitud) { return ResponseEntity.ok(experienciaServicio.actualizar(id, solicitud)); }
    @Override public ResponseEntity<Void> eliminarLogicamente(Long id) { experienciaServicio.eliminarLogicamente(id); return ResponseEntity.noContent().build(); }
}
