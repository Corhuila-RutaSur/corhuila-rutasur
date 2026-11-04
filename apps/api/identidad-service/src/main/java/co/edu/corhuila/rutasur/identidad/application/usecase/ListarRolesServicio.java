package co.edu.corhuila.rutasur.identidad.application.usecase;

import co.edu.corhuila.rutasur.identidad.application.port.in.ListarRolesUseCase;
import co.edu.corhuila.rutasur.identidad.application.port.out.RolPuertoSalida;
import co.edu.corhuila.rutasur.identidad.domain.model.RolUsuario;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Consulta roles activos desde la fuente de verdad de Identidad.
 */
@Service
@RequiredArgsConstructor
public class ListarRolesServicio implements ListarRolesUseCase {
    private final RolPuertoSalida roles;

    @Override
    public List<RolUsuario> listar() {
        return roles.listarActivos();
    }
}
