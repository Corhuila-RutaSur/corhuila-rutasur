package co.edu.corhuila.rutasur.identidad.adapter.out.persistence;

import co.edu.corhuila.rutasur.identidad.application.port.out.UsuarioAdministracionPuertoSalida;
import co.edu.corhuila.rutasur.identidad.domain.model.RolUsuario;
import co.edu.corhuila.rutasur.identidad.domain.model.UsuarioAdministrable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Adaptador de lectura administrativa con proyecciones SQL y roles agrupados.
 */
@Component
@RequiredArgsConstructor
public class UsuarioAdministracionPersistenciaAdaptador implements UsuarioAdministracionPuertoSalida {
    private final JdbcTemplate jdbcTemplate;
    private static final String BASE = "select u.id, u.tipo_documento, u.numero_documento, u.nombres, u.apellidos, u.correo, u.telefono, u.activo, u.creado_en, r.nombre as rol_nombre from usuario u left join usuario_rol ur on ur.usuario_id = u.id left join rol r on r.id = ur.rol_id and r.activo = true ";

    @Override
    public List<UsuarioAdministrable> listar() {
        return jdbcTemplate.query(BASE + "order by u.creado_en desc", this::mapear);
    }

    @Override
    public Optional<UsuarioAdministrable> buscarPorId(UUID usuarioId) {
        return jdbcTemplate.query(BASE + "where u.id = ?", this::mapear, usuarioId).stream().findFirst();
    }

    @Override
    public void actualizar(UUID usuarioId, String nombres, String apellidos, String correo, String telefono) {
        jdbcTemplate.update("update usuario set nombres = ?, apellidos = ?, correo = ?, telefono = ?, actualizado_en = current_timestamp where id = ?",
                nombres, apellidos, correo, telefono, usuarioId);
    }

    @Override
    public void reemplazarRoles(UUID usuarioId, List<RolUsuario> roles) {
        jdbcTemplate.update("delete from usuario_rol where usuario_id = ?", usuarioId);
        roles.stream().distinct().forEach(rol -> jdbcTemplate.update("insert into usuario_rol (usuario_id, rol_id) select ?, id from rol where nombre = ? and activo = true",
                usuarioId, rol.name()));
    }

    @Override
    public void inactivar(UUID usuarioId) {
        jdbcTemplate.update("update usuario set activo = false, actualizado_en = current_timestamp where id = ? and activo = true", usuarioId);
    }

    private List<UsuarioAdministrable> mapear(ResultSet resultados) throws SQLException {
        Map<UUID, List<RolUsuario>> rolesPorUsuario = new LinkedHashMap<>();
        Map<UUID, UsuarioAdministrable> usuarios = new LinkedHashMap<>();
        while (resultados.next()) {
            UUID id = resultados.getObject("id", UUID.class);
            List<RolUsuario> roles = rolesPorUsuario.get(id);
            if (roles == null) {
                roles = new ArrayList<>();
                rolesPorUsuario.put(id, roles);
                usuarios.put(id, new UsuarioAdministrable(id, resultados.getString("tipo_documento"), resultados.getString("numero_documento"), resultados.getString("nombres"), resultados.getString("apellidos"), resultados.getString("correo"), resultados.getString("telefono"), resultados.getBoolean("activo"), resultados.getObject("creado_en", java.time.OffsetDateTime.class), roles));
            }
            String rol = resultados.getString("rol_nombre");
            if (rol != null) roles.add(RolUsuario.valueOf(rol));
        }
        return new ArrayList<>(usuarios.values());
    }
}
