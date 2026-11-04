package co.edu.corhuila.rutasur.identidad.adapter.in.http;

import co.edu.corhuila.rutasur.identidad.application.port.in.CerrarSesionCommand;
import co.edu.corhuila.rutasur.identidad.application.port.in.CerrarSesionUseCase;
import co.edu.corhuila.rutasur.identidad.domain.exception.ExcepcionAutenticacion;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Adaptador REST para cerrar la sesión del usuario autenticado.
 */
@RestController
@RequiredArgsConstructor
public class SesionControlador implements SesionApi {
    private final CerrarSesionUseCase cerrarSesion;
    private final MessageSource mensajes;

    /**
     * Verifica formato Bearer y delega el cierre de la sesión al caso de uso.
     */
    @Override
    public ResponseEntity<RespuestaGeneral<Void>> cerrarSesion(String autorizacion, UUID correlacionId) {
        if (autorizacion == null || !autorizacion.startsWith("Bearer ")) {
            throw new ExcepcionAutenticacion("autenticacion.token.invalido");
        }
        UUID correlacionEfectiva = correlacionId == null ? UUID.randomUUID() : correlacionId;
        cerrarSesion.cerrar(new CerrarSesionCommand(autorizacion.substring(7), correlacionEfectiva));
        String mensaje = mensajes.getMessage("autenticacion.logout.exitoso", null, LocaleContextHolder.getLocale());
        return ResponseEntity.ok().header("X-Correlation-Id", correlacionEfectiva.toString())
                .body(RespuestaGeneral.exitosa("200", "autenticacion.logout.exitoso", mensaje, null));
    }
}
