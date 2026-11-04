package co.edu.corhuila.rutasur.identidad.adapter.out.persistence;

import co.edu.corhuila.rutasur.identidad.application.port.out.PerfilUsuarioPuertoSalida;
import co.edu.corhuila.rutasur.identidad.domain.model.PerfilUsuario;
import co.edu.corhuila.rutasur.identidad.domain.model.RolUsuario;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Adaptador de consulta que devuelve una proyección segura del perfil de usuario.
 */
@Component
@RequiredArgsConstructor
public class PerfilUsuarioPersistenciaAdaptador implements PerfilUsuarioPuertoSalida {
    private final JdbcTemplate jdbcTemplate;

    /**
     * Lee datos del perfil y roles activos sin cargar el agregado JPA completo.
     */
    @Override
    public Optional<PerfilUsuario> buscarPerfil(UUID usuarioId) {
        String sql = "select u.id, u.tipo_documento, u.numero_documento, u.nombres, u.apellidos, u.correo, u.telefono, u.activo, r.nombre as rol_nombre "
                + "from usuario u left join usuario_rol ur on ur.usuario_id = u.id left join rol r on r.id = ur.rol_id and r.activo = true where u.id = ?";
        return jdbcTemplate.query(sql, this::mapearPerfil, usuarioId).stream().findFirst();
    }

    private List<PerfilUsuario> mapearPerfil(ResultSet resultados) throws SQLException {
        List<PerfilUsuario> perfiles = new ArrayList<>();
        PerfilUsuario perfil = null;
        List<RolUsuario> roles = new ArrayList<>();
        while (resultados.next()) {
            if (perfil == null) {
                perfil = new PerfilUsuario(resultados.getObject("id", UUID.class), resultados.getString("tipo_documento"),
                        resultados.getString("numero_documento"), resultados.getString("nombres"), resultados.getString("apellidos"),
                        resultados.getString("correo"), resultados.getString("telefono"), resultados.getBoolean("activo"), roles);
            }
            String rol = resultados.getString("rol_nombre");
            if (rol != null) roles.add(RolUsuario.valueOf(rol));
        }
        if (perfil != null) perfiles.add(perfil);
        return perfiles;
    }
}
