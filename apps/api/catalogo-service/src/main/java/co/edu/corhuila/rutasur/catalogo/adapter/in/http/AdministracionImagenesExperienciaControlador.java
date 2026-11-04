package co.edu.corhuila.rutasur.catalogo.adapter.in.http;

import co.edu.corhuila.rutasur.catalogo.application.port.in.ActualizarImagenExperienciaCommand;
import co.edu.corhuila.rutasur.catalogo.application.port.in.ArchivoImagenCommand;
import co.edu.corhuila.rutasur.catalogo.application.port.in.GestionarImagenExperienciaUseCase;
import co.edu.corhuila.rutasur.catalogo.domain.model.ImagenExperiencia;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/** Adaptador HTTP para cargar y ordenar imágenes sin exponer la infraestructura de archivos. */
@RestController
@RequiredArgsConstructor
public class AdministracionImagenesExperienciaControlador implements AdministracionImagenesExperienciaApi {
    private final GestionarImagenExperienciaUseCase imagenes;
    private final MessageSource mensajes;
    @Override public ResponseEntity<RespuestaGeneral<ImagenExperienciaResponse>> cargar(UUID experienciaId, MultipartFile archivo, boolean principal, int orden, UUID correlacionId) {
        UUID correlacion = correlacionId == null ? UUID.randomUUID() : correlacionId;
        try {
            ImagenExperiencia imagen = imagenes.cargar(experienciaId, new ArchivoImagenCommand(archivo.getOriginalFilename(), archivo.getContentType(), archivo.getBytes()), principal, orden, correlacion);
            return ResponseEntity.status(HttpStatus.CREATED).header("X-Correlation-Id", correlacion.toString()).body(RespuestaGeneral.exitosa("201", "imagen.cargada", mensaje("imagen.cargada"), respuesta(imagen)));
        } catch (IOException error) { throw new IllegalArgumentException("No fue posible leer el archivo adjunto.", error); }
    }
    @Override public ResponseEntity<RespuestaGeneral<List<ImagenExperienciaResponse>>> listar(UUID experienciaId) { return ResponseEntity.ok(RespuestaGeneral.exitosa("200", "imagen.consultada", mensaje("imagen.consultada"), imagenes.listar(experienciaId).stream().map(this::respuesta).toList())); }
    @Override public ResponseEntity<RespuestaGeneral<ImagenExperienciaResponse>> actualizar(UUID experienciaId, UUID imagenId, ImagenExperienciaActualizacionRequest solicitud, UUID correlacionId) {
        UUID correlacion = correlacionId == null ? UUID.randomUUID() : correlacionId;
        ImagenExperiencia imagen = imagenes.actualizar(experienciaId, imagenId, new ActualizarImagenExperienciaCommand(solicitud.principal(), solicitud.orden(), correlacion));
        return ResponseEntity.ok().header("X-Correlation-Id", correlacion.toString()).body(RespuestaGeneral.exitosa("200", "imagen.actualizada", mensaje("imagen.actualizada"), respuesta(imagen)));
    }
    @Override public ResponseEntity<RespuestaGeneral<Void>> inactivar(UUID experienciaId, UUID imagenId, UUID correlacionId) {
        UUID correlacion = correlacionId == null ? UUID.randomUUID() : correlacionId; imagenes.inactivar(experienciaId, imagenId, correlacion);
        return ResponseEntity.ok().header("X-Correlation-Id", correlacion.toString()).body(RespuestaGeneral.exitosa("200", "imagen.inactivada", mensaje("imagen.inactivada"), null));
    }
    private ImagenExperienciaResponse respuesta(ImagenExperiencia i) { return new ImagenExperienciaResponse(i.id(), i.experienciaId(), i.ruta(), i.principal(), i.orden(), i.activa(), i.creadoEn(), i.actualizadoEn()); }
    private String mensaje(String codigo) { return mensajes.getMessage(codigo, null, LocaleContextHolder.getLocale()); }
}
