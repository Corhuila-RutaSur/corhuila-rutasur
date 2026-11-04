package co.edu.corhuila.rutasur.identidad.adapter.out.persistence;

import co.edu.corhuila.rutasur.identidad.application.port.out.RolPuertoSalida;
import co.edu.corhuila.rutasur.identidad.domain.model.RolUsuario;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Adaptador que traduce el catálogo persistido de roles a valores del dominio.
 */
@Component
@RequiredArgsConstructor
public class RolPersistenciaAdaptador implements RolPuertoSalida {
    private final RolRepositorioJpa roles;

    /**
     * Devuelve exclusivamente los roles que permanecen habilitados en base de datos.
     */
    @Override
    public List<RolUsuario> listarActivos() {
        return roles.findAllByActivoTrueOrderByNombreAsc().stream().map(rol -> RolUsuario.valueOf(rol.getNombre())).toList();
    }

    /**
     * Comprueba en la base de datos que cada rol solicitado siga habilitado.
     */
    @Override
    public boolean todosActivos(List<RolUsuario> rolesSolicitados) {
        if (rolesSolicitados == null || rolesSolicitados.isEmpty()) return false;
        return rolesSolicitados.stream().allMatch(rol -> roles.findByNombreAndActivoTrue(rol.name()).isPresent());
    }
}
