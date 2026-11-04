package co.edu.corhuila.rutasur.identidad.adapter.out.persistence;

import co.edu.corhuila.rutasur.identidad.application.port.out.UsuarioPuertoSalida;
import co.edu.corhuila.rutasur.identidad.domain.exception.ExcepcionReglaNegocio;
import co.edu.corhuila.rutasur.identidad.domain.model.RolUsuario;
import co.edu.corhuila.rutasur.identidad.domain.model.Usuario;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Adaptador JPA que implementa el puerto de usuarios.
 */
@Component
@RequiredArgsConstructor
public class UsuarioPersistenciaAdaptador implements UsuarioPuertoSalida {
    private final UsuarioRepositorioJpa usuarios;
    private final RolRepositorioJpa roles;
    private final UsuarioPersistenciaMapper mapper;

    @Override
    public boolean existeNumeroDocumento(String valor) {
        return usuarios.existsByNumeroDocumento(valor);
    }

    @Override
    public boolean existeCorreo(String valor) {
        return usuarios.existsByCorreoIgnoreCase(valor);
    }

    @Override
    public boolean existeTelefono(String valor) {
        return usuarios.existsByTelefono(valor);
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        return mapper.aDominio(usuarios.save(mapper.aEntidad(usuario)));
    }

    @Override
    public void asignarRol(UUID usuarioId, RolUsuario rol) {
        RolEntidad rolEntidad = roles.findByNombreAndActivoTrue(rol.name())
                .orElseThrow(() -> new ExcepcionReglaNegocio("usuario.rol_cliente.no_encontrado"));
        usuarios.asignarRol(usuarioId, rolEntidad.getId());
    }
}
