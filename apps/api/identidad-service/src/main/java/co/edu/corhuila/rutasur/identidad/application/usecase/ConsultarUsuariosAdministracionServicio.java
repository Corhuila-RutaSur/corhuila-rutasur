package co.edu.corhuila.rutasur.identidad.application.usecase;

import co.edu.corhuila.rutasur.identidad.application.port.in.ConsultarUsuariosAdministracionUseCase;
import co.edu.corhuila.rutasur.identidad.application.port.out.UsuarioAdministracionPuertoSalida;
import co.edu.corhuila.rutasur.identidad.domain.exception.ExcepcionReglaNegocio;
import co.edu.corhuila.rutasur.identidad.domain.model.UsuarioAdministrable;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Consulta proyecciones administrativas sin exponer agregados internos.
 */
@Service
@RequiredArgsConstructor
public class ConsultarUsuariosAdministracionServicio implements ConsultarUsuariosAdministracionUseCase {
    private final UsuarioAdministracionPuertoSalida usuarios;

    @Override
    public List<UsuarioAdministrable> listar() {
        return usuarios.listar();
    }

    @Override
    public UsuarioAdministrable consultar(UUID usuarioId) {
        return usuarios.buscarPorId(usuarioId).orElseThrow(() -> new ExcepcionReglaNegocio("usuario.no_encontrado"));
    }
}
