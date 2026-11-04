package co.edu.corhuila.rutasur.catalogo.adapter.in.http;

import co.edu.corhuila.rutasur.catalogo.adapter.out.storage.AlmacenamientoImagenLocalAdaptador;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/** Publica solamente archivos que pertenezcan a la ruta segura del almacenamiento local. */
@RestController
@RequiredArgsConstructor
public class ArchivosPublicosControlador {
    private final AlmacenamientoImagenLocalAdaptador almacenamiento;
    @GetMapping("/archivos/experiencias/{experienciaId}/{archivo:.+}")
    public ResponseEntity<FileSystemResource> obtener(@PathVariable String experienciaId, @PathVariable String archivo) throws Exception {
        Path ruta = almacenamiento.resolver("/archivos/experiencias/" + experienciaId + "/" + archivo);
        if (!Files.isRegularFile(ruta)) return ResponseEntity.notFound().build();
        String tipo = Files.probeContentType(ruta);
        return ResponseEntity.ok().contentType(tipo == null ? MediaType.APPLICATION_OCTET_STREAM : MediaType.parseMediaType(tipo)).body(new FileSystemResource(ruta));
    }
}
