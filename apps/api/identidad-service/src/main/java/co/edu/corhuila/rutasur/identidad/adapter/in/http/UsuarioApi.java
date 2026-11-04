package co.edu.corhuila.rutasur.identidad.adapter.in.http;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Contrato protegido de consulta del usuario autenticado.
 */
@RequestMapping("/api/v2/usuarios")
public interface UsuarioApi {

    /**
     * Devuelve el perfil propio identificado por el JWT previamente validado.
     */
    @GetMapping("/me")
    ResponseEntity<RespuestaGeneral<PerfilUsuarioResponse>> consultarMiPerfil(@AuthenticationPrincipal UUID usuarioId);
}
