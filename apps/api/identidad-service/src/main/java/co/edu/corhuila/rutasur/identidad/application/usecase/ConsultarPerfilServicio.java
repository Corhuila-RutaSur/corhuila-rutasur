package co.edu.corhuila.rutasur.identidad.application.usecase;

import co.edu.corhuila.rutasur.identidad.application.port.in.ConsultarPerfilUseCase;
import co.edu.corhuila.rutasur.identidad.application.port.out.PerfilUsuarioPuertoSalida;
import co.edu.corhuila.rutasur.identidad.domain.exception.ExcepcionReglaNegocio;
import co.edu.corhuila.rutasur.identidad.domain.model.PerfilUsuario;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Consulta el perfil propio desde una proyección de lectura segura.
 */
@Service
@RequiredArgsConstructor
public class ConsultarPerfilServicio implements ConsultarPerfilUseCase {
    private final PerfilUsuarioPuertoSalida perfiles;

    /**
     * Obtiene el perfil o informa que la identidad ya no se encuentra disponible.
     */
    @Override
    public PerfilUsuario consultar(UUID usuarioId) {
        return perfiles.buscarPerfil(usuarioId).orElseThrow(() -> new ExcepcionReglaNegocio("usuario.no_encontrado"));
    }
}
