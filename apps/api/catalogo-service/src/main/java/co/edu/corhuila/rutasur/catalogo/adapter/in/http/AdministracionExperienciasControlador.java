package co.edu.corhuila.rutasur.catalogo.adapter.in.http;

import co.edu.corhuila.rutasur.catalogo.application.port.in.CrearExperienciaUseCase;
import co.edu.corhuila.rutasur.catalogo.application.port.in.GestionarExperienciaUseCase;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Adaptador REST de creación administrativa de experiencias.
 */
@RestController
@RequiredArgsConstructor
public class AdministracionExperienciasControlador implements AdministracionExperienciasApi {
    private final CrearExperienciaUseCase crearExperiencia;
    private final GestionarExperienciaUseCase gestionarExperiencias;
    private final ExperienciaHttpMapper mapper;
    private final MessageSource mensajes;

    @Override
    public ResponseEntity<RespuestaGeneral<ExperienciaResponse>> crear(ExperienciaCreacionRequest solicitud, UUID correlacionId) {
        UUID correlacionEfectiva = correlacionId == null ? UUID.randomUUID() : correlacionId;
        return ResponseEntity.status(HttpStatus.CREATED).header("X-Correlation-Id", correlacionEfectiva.toString())
                .body(RespuestaGeneral.exitosa("201", "experiencia.creada", mensajes.getMessage("experiencia.creada", null, LocaleContextHolder.getLocale()), mapper.aRespuesta(crearExperiencia.crear(mapper.aComando(solicitud, correlacionEfectiva)))));
    }

    @Override
    public ResponseEntity<RespuestaGeneral<List<ExperienciaResponse>>> consultarActivas() {
        List<ExperienciaResponse> respuestas = gestionarExperiencias.consultarActivas().stream().map(mapper::aRespuesta).toList();
        return ResponseEntity.ok(RespuestaGeneral.exitosa("200", "experiencia.consultada", mensaje("experiencia.consultada"), respuestas));
    }

    @Override
    public ResponseEntity<RespuestaGeneral<ExperienciaResponse>> consultarPorId(UUID identificador) {
        return ResponseEntity.ok(RespuestaGeneral.exitosa("200", "experiencia.consultada", mensaje("experiencia.consultada"), mapper.aRespuesta(gestionarExperiencias.consultarPorId(identificador))));
    }

    @Override
    public ResponseEntity<RespuestaGeneral<ExperienciaResponse>> actualizar(UUID identificador, ExperienciaCreacionRequest solicitud, UUID correlacionId) {
        UUID correlacionEfectiva = correlacionId == null ? UUID.randomUUID() : correlacionId;
        return ResponseEntity.ok().header("X-Correlation-Id", correlacionEfectiva.toString()).body(RespuestaGeneral.exitosa("200", "experiencia.actualizada", mensaje("experiencia.actualizada"), mapper.aRespuesta(gestionarExperiencias.actualizar(identificador, mapper.aComandoActualizar(solicitud, correlacionEfectiva)))));
    }

    @Override
    public ResponseEntity<RespuestaGeneral<Void>> inactivar(UUID identificador) {
        gestionarExperiencias.inactivar(identificador);
        return ResponseEntity.ok(RespuestaGeneral.exitosa("200", "experiencia.inactivada", mensaje("experiencia.inactivada"), null));
    }

    private String mensaje(String codigo) {
        return mensajes.getMessage(codigo, null, LocaleContextHolder.getLocale());
    }
}
