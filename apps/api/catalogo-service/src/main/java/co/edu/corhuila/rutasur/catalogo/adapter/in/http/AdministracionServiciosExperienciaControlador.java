package co.edu.corhuila.rutasur.catalogo.adapter.in.http;

import co.edu.corhuila.rutasur.catalogo.application.port.in.GestionarServicioExperienciaUseCase;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Adaptador REST para administrar servicios pertenecientes a una experiencia.
 */
@RestController
@RequiredArgsConstructor
public class AdministracionServiciosExperienciaControlador implements AdministracionServiciosExperienciaApi {
    private final GestionarServicioExperienciaUseCase gestionarServicios;
    private final ServicioExperienciaHttpMapper mapper;
    private final MessageSource mensajes;

    @Override
    public ResponseEntity<RespuestaGeneral<ServicioExperienciaResponse>> crear(UUID experienciaId, ServicioExperienciaRequest solicitud, UUID correlacionId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(RespuestaGeneral.exitosa("201", "servicio.experiencia.creado", mensaje("servicio.experiencia.creado"), mapper.aRespuesta(gestionarServicios.crear(experienciaId, mapper.aComando(solicitud, correlacionId)))));
    }

    @Override
    public ResponseEntity<RespuestaGeneral<List<ServicioExperienciaResponse>>> consultarActivos(UUID experienciaId) {
        return ResponseEntity.ok(RespuestaGeneral.exitosa("200", "servicio.experiencia.consultado", mensaje("servicio.experiencia.consultado"), gestionarServicios.consultarActivos(experienciaId).stream().map(mapper::aRespuesta).toList()));
    }

    @Override
    public ResponseEntity<RespuestaGeneral<ServicioExperienciaResponse>> actualizar(UUID experienciaId, UUID servicioId, ServicioExperienciaRequest solicitud, UUID correlacionId) {
        return ResponseEntity.ok(RespuestaGeneral.exitosa("200", "servicio.experiencia.actualizado", mensaje("servicio.experiencia.actualizado"), mapper.aRespuesta(gestionarServicios.actualizar(experienciaId, servicioId, mapper.aComando(solicitud, correlacionId)))));
    }

    @Override
    public ResponseEntity<RespuestaGeneral<Void>> inactivar(UUID experienciaId, UUID servicioId) {
        gestionarServicios.inactivar(experienciaId, servicioId);
        return ResponseEntity.ok(RespuestaGeneral.exitosa("200", "servicio.experiencia.inactivado", mensaje("servicio.experiencia.inactivado"), null));
    }

    private String mensaje(String codigo) {
        return mensajes.getMessage(codigo, null, LocaleContextHolder.getLocale());
    }
}
