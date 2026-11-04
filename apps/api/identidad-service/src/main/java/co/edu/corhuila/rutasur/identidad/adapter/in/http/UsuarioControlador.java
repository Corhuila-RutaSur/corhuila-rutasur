package co.edu.corhuila.rutasur.identidad.adapter.in.http;

import co.edu.corhuila.rutasur.identidad.application.port.in.ConsultarPerfilUseCase;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Adaptador REST del perfil propio protegido por el filtro JWT.
 */
@RestController
@RequiredArgsConstructor
public class UsuarioControlador implements UsuarioApi {
    private final ConsultarPerfilUseCase consultarPerfil;
    private final PerfilUsuarioHttpMapper mapper;
    private final MessageSource mensajes;

    /**
     * Expone únicamente la proyección segura del usuario autenticado.
     */
    @Override
    public ResponseEntity<RespuestaGeneral<PerfilUsuarioResponse>> consultarMiPerfil(UUID usuarioId) {
        String mensaje = mensajes.getMessage("usuario.perfil.consultado", null, LocaleContextHolder.getLocale());
        return ResponseEntity.ok(RespuestaGeneral.exitosa("200", "usuario.perfil.consultado", mensaje,
                mapper.aRespuesta(consultarPerfil.consultar(usuarioId))));
    }
}
