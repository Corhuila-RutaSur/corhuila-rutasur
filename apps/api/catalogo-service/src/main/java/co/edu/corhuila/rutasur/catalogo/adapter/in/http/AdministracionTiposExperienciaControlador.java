package co.edu.corhuila.rutasur.catalogo.adapter.in.http;

import co.edu.corhuila.rutasur.catalogo.application.port.in.GestionarTipoExperienciaUseCase;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Adaptador de entrada REST para administrar tipos de experiencia.
 */
@RestController
@RequiredArgsConstructor
public class AdministracionTiposExperienciaControlador implements AdministracionTiposExperienciaApi {
    private final GestionarTipoExperienciaUseCase gestionarTipos;
    private final TipoExperienciaHttpMapper mapper;
    private final MessageSource mensajes;

    @Override
    public ResponseEntity<RespuestaGeneral<TipoExperienciaResponse>> crear(TipoExperienciaRequest solicitud, UUID correlacionId) {
        TipoExperienciaResponse respuesta = mapper.aRespuesta(gestionarTipos.crear(mapper.aComandoCrear(solicitud, correlacionId)));
        return ResponseEntity.status(HttpStatus.CREATED).body(RespuestaGeneral.exitosa("201", "tipo.experiencia.creado", mensaje("tipo.experiencia.creado"), respuesta));
    }

    @Override
    public ResponseEntity<RespuestaGeneral<TipoExperienciaResponse>> actualizar(UUID identificador, TipoExperienciaRequest solicitud, UUID correlacionId) {
        TipoExperienciaResponse respuesta = mapper.aRespuesta(gestionarTipos.actualizar(identificador, mapper.aComandoActualizar(solicitud, correlacionId)));
        return ResponseEntity.ok(RespuestaGeneral.exitosa("200", "tipo.experiencia.actualizado", mensaje("tipo.experiencia.actualizado"), respuesta));
    }

    @Override
    public ResponseEntity<RespuestaGeneral<List<TipoExperienciaResponse>>> consultarActivos() {
        List<TipoExperienciaResponse> respuesta = gestionarTipos.consultarActivos().stream().map(mapper::aRespuesta).toList();
        return ResponseEntity.ok(RespuestaGeneral.exitosa("200", "tipo.experiencia.consultado", mensaje("tipo.experiencia.consultado"), respuesta));
    }

    @Override
    public ResponseEntity<RespuestaGeneral<Void>> inactivar(UUID identificador) {
        gestionarTipos.inactivar(identificador);
        return ResponseEntity.ok(RespuestaGeneral.exitosa("200", "tipo.experiencia.inactivado", mensaje("tipo.experiencia.inactivado"), null));
    }

    private String mensaje(String codigo) {
        return mensajes.getMessage(codigo, null, LocaleContextHolder.getLocale());
    }
}
