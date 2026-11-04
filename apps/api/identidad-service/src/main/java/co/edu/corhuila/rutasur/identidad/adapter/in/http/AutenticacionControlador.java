package co.edu.corhuila.rutasur.identidad.adapter.in.http;

import co.edu.corhuila.rutasur.identidad.application.port.in.IniciarSesionUseCase;
import co.edu.corhuila.rutasur.identidad.application.port.in.RenovarSesionUseCase;
import co.edu.corhuila.rutasur.identidad.domain.model.SesionIniciada;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Adaptador REST que expone login sin acoplar HTTP a reglas de autenticación.
 */
@RestController
@RequiredArgsConstructor
public class AutenticacionControlador implements AutenticacionApi {
    private final IniciarSesionUseCase iniciarSesion;
    private final RenovarSesionUseCase renovarSesion;
    private final AutenticacionHttpMapper mapper;
    private final MessageSource mensajes;

    /**
     * Ejecuta login y entrega tokens con el envoltorio uniforme del servicio.
     */
    @Override
    public ResponseEntity<RespuestaGeneral<AutenticacionLoginResponse>> iniciarSesion(AutenticacionLoginRequest solicitud,
                                                                                      UUID correlacionId, String ipOrigen, String agenteUsuario) {
        UUID correlacionEfectiva = correlacionId == null ? UUID.randomUUID() : correlacionId;
        SesionIniciada resultado = iniciarSesion.iniciar(mapper.aComando(solicitud, ipOrigen, agenteUsuario, correlacionEfectiva));
        String mensaje = mensajes.getMessage("autenticacion.login.exitoso", null, LocaleContextHolder.getLocale());
        return ResponseEntity.status(HttpStatus.OK)
                .header("X-Correlation-Id", correlacionEfectiva.toString())
                .body(RespuestaGeneral.exitosa("200", "autenticacion.login.exitoso", mensaje, mapper.aRespuesta(resultado)));
    }

    /**
     * Rota el refresh token y devuelve tokens vigentes sin extender su vencimiento máximo.
     */
    @Override
    public ResponseEntity<RespuestaGeneral<AutenticacionLoginResponse>> renovarSesion(AutenticacionRefreshRequest solicitud,
                                                                                      UUID correlacionId) {
        UUID correlacionEfectiva = correlacionId == null ? UUID.randomUUID() : correlacionId;
        SesionIniciada resultado = renovarSesion.renovar(mapper.aComando(solicitud, correlacionEfectiva));
        String mensaje = mensajes.getMessage("autenticacion.refresh.exitoso", null, LocaleContextHolder.getLocale());
        return ResponseEntity.ok().header("X-Correlation-Id", correlacionEfectiva.toString())
                .body(RespuestaGeneral.exitosa("200", "autenticacion.refresh.exitoso", mensaje, mapper.aRespuesta(resultado)));
    }
}
