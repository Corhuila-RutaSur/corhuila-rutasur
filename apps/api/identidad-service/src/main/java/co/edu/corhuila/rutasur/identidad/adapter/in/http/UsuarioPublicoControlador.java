package co.edu.corhuila.rutasur.identidad.adapter.in.http;

import co.edu.corhuila.rutasur.identidad.application.port.in.RegistrarClienteUseCase;
import co.edu.corhuila.rutasur.identidad.domain.model.Usuario;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Adaptador REST que expone el registro público sin conocer la persistencia.
 */
@RestController
@RequiredArgsConstructor
public class UsuarioPublicoControlador implements UsuarioPublicoApi {
    private final RegistrarClienteUseCase registrarCliente;
    private final UsuarioHttpMapper mapper;
    private final MessageSource mensajes;

    /**
     * Ejecuta el caso de uso de registro público y devuelve la estructura estándar.
     */
    @Override
    public ResponseEntity<RespuestaGeneral<UsuarioRegistroResponse>> registrar(
            UsuarioRegistroRequest solicitud, UUID correlacionId) {
        UUID correlacionEfectiva = correlacionId == null ? UUID.randomUUID() : correlacionId;
        Usuario usuario = registrarCliente.registrar(mapper.aComando(solicitud, correlacionEfectiva));
        String mensaje = mensajes.getMessage("usuario.registro.exitoso", null, LocaleContextHolder.getLocale());
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("X-Correlation-Id", correlacionEfectiva.toString())
                .body(RespuestaGeneral.exitosa("201", "usuario.registrado", mensaje, mapper.aRespuesta(usuario)));
    }
}
