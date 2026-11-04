package co.edu.corhuila.rutasur.catalogo.adapter.in.http;

import co.edu.corhuila.rutasur.catalogo.application.port.in.GestionarActividadUseCase;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Adaptador REST de administración de actividades.
 */
@RestController
@RequiredArgsConstructor
public class AdministracionActividadesControlador implements AdministracionActividadesApi {
    private final GestionarActividadUseCase gestionarActividades;
    private final ActividadHttpMapper mapper;
    private final MessageSource mensajes;

    @Override
    public ResponseEntity<RespuestaGeneral<ActividadResponse>> crear(UUID experienciaId, ActividadRequest solicitud, UUID correlacionId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(RespuestaGeneral.exitosa("201", "actividad.creada", mensaje("actividad.creada"), mapper.aRespuesta(gestionarActividades.crear(experienciaId, mapper.aComando(solicitud, correlacionId)))));
    }

    @Override
    public ResponseEntity<RespuestaGeneral<List<ActividadResponse>>> consultarActivas(UUID experienciaId) {
        return ResponseEntity.ok(RespuestaGeneral.exitosa("200", "actividad.consultada", mensaje("actividad.consultada"), gestionarActividades.consultarActivas(experienciaId).stream().map(mapper::aRespuesta).toList()));
    }

    @Override
    public ResponseEntity<RespuestaGeneral<ActividadResponse>> actualizar(UUID experienciaId, UUID actividadId, ActividadRequest solicitud, UUID correlacionId) {
        return ResponseEntity.ok(RespuestaGeneral.exitosa("200", "actividad.actualizada", mensaje("actividad.actualizada"), mapper.aRespuesta(gestionarActividades.actualizar(experienciaId, actividadId, mapper.aComando(solicitud, correlacionId)))));
    }

    @Override
    public ResponseEntity<RespuestaGeneral<Void>> inactivar(UUID experienciaId, UUID actividadId) {
        gestionarActividades.inactivar(experienciaId, actividadId);
        return ResponseEntity.ok(RespuestaGeneral.exitosa("200", "actividad.inactivada", mensaje("actividad.inactivada"), null));
    }

    private String mensaje(String codigo) {
        return mensajes.getMessage(codigo, null, LocaleContextHolder.getLocale());
    }
}
