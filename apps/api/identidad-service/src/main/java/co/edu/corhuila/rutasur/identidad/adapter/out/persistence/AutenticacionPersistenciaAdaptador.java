package co.edu.corhuila.rutasur.identidad.adapter.out.persistence;

import co.edu.corhuila.rutasur.identidad.application.port.out.AutenticacionPuertoSalida;
import co.edu.corhuila.rutasur.identidad.domain.model.RolUsuario;
import co.edu.corhuila.rutasur.identidad.domain.model.RefreshTokenAutenticable;
import co.edu.corhuila.rutasur.identidad.domain.model.SesionAutenticacion;
import co.edu.corhuila.rutasur.identidad.domain.model.UsuarioAutenticable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Adaptador PostgreSQL para credenciales, sesiones y refresh tokens de Identidad.
 */
@Component
@RequiredArgsConstructor
public class AutenticacionPersistenciaAdaptador implements AutenticacionPuertoSalida {
    private final JdbcTemplate jdbcTemplate;

    /**
     * Obtiene credenciales y roles activos sin exponer entidades JPA al caso de uso.
     */
    @Override
    public Optional<UsuarioAutenticable> buscarUsuarioAutenticablePorCorreo(String correo) {
        String sql = "select u.id, u.correo, u.password_hash, u.activo, r.nombre as rol_nombre "
                + "from usuario u left join usuario_rol ur on ur.usuario_id = u.id "
                + "left join rol r on r.id = ur.rol_id and r.activo = true "
                + "where lower(u.correo) = lower(?)";
        List<UsuarioAutenticable> usuarios = jdbcTemplate.query(sql, this::mapearUsuarioAutenticable, correo.trim().toLowerCase(Locale.ROOT));
        return usuarios.stream().findFirst();
    }

    /**
     * Inserta una sesión activa con su vencimiento máximo.
     */
    @Override
    public SesionAutenticacion crearSesion(UUID usuarioId, String ipOrigen, String agenteUsuario, OffsetDateTime expiraEn) {
        UUID sesionId = jdbcTemplate.queryForObject(
                "insert into sesion_autenticacion (usuario_id, estado, ip_origen, agente_usuario, expira_en) "
                        + "values (?, 'ACTIVA', ?, ?, ?) returning id",
                UUID.class, usuarioId, ipOrigen, agenteUsuario, expiraEn);
        return new SesionAutenticacion(sesionId, usuarioId, expiraEn);
    }

    /**
     * Persiste el hash de un refresh token, nunca el valor que recibe el cliente.
     */
    @Override
    public UUID guardarRefreshToken(UUID sesionId, String tokenHash, OffsetDateTime expiraEn) {
        return jdbcTemplate.queryForObject("insert into refresh_token (sesion_id, token_hash, expira_en) values (?, ?, ?) returning id",
                UUID.class, sesionId, tokenHash, expiraEn);
    }

    /**
     * Bloquea la fila del refresh para que solo una transacción pueda rotarlo.
     */
    @Override
    public Optional<RefreshTokenAutenticable> buscarRefreshParaRotacion(String tokenHash) {
        String sql = "select rt.id as refresh_id, rt.sesion_id, rt.usado, rt.expira_en, s.usuario_id, s.estado as sesion_estado, "
                + "u.correo, u.activo as usuario_activo, r.nombre as rol_nombre "
                + "from refresh_token rt join sesion_autenticacion s on s.id = rt.sesion_id "
                + "join usuario u on u.id = s.usuario_id left join usuario_rol ur on ur.usuario_id = u.id "
                + "left join rol r on r.id = ur.rol_id and r.activo = true where rt.token_hash = ? for update of rt";
        List<RefreshTokenAutenticable> refreshes = jdbcTemplate.query(sql, this::mapearRefresh, tokenHash);
        return refreshes.stream().findFirst();
    }

    /**
     * Registra la rotación de manera explícita; un refresh no puede volver a utilizarse.
     */
    @Override
    public void marcarRefreshComoUsado(UUID refreshId, UUID refreshReemplazoId) {
        jdbcTemplate.update("update refresh_token set usado = true, usado_en = current_timestamp, reemplazado_por_id = ? where id = ?",
                refreshReemplazoId, refreshId);
    }

    /**
     * Invalida toda la sesión y sus futuros usos sin eliminar evidencia operativa.
     */
    @Override
    public void invalidarSesion(UUID sesionId) {
        jdbcTemplate.update("update sesion_autenticacion set estado = 'INVALIDADA', invalidada_en = current_timestamp "
                + "where id = ? and estado = 'ACTIVA'", sesionId);
    }

    @Override
    public void invalidarSesionesUsuario(UUID usuarioId) {
        jdbcTemplate.update("update sesion_autenticacion set estado = 'INVALIDADA', invalidada_en = current_timestamp where usuario_id = ? and estado = 'ACTIVA'", usuarioId);
    }

    /**
     * Consulta la vigencia operativa de una sesión emitida previamente.
     */
    @Override
    public boolean sesionActiva(UUID sesionId) {
        Integer cantidad = jdbcTemplate.queryForObject("select count(1) from sesion_autenticacion where id = ? and estado = 'ACTIVA' and expira_en > current_timestamp",
                Integer.class, sesionId);
        return cantidad != null && cantidad > 0;
    }

    /**
     * Consulta revocación de access token por su identificador JWT único.
     */
    @Override
    public boolean accessTokenRevocado(String jti) {
        Integer cantidad = jdbcTemplate.queryForObject("select count(1) from access_token_revocado where jti = ?", Integer.class, jti);
        return cantidad != null && cantidad > 0;
    }

    /**
     * Persiste la revocación lógica e idempotente de un access token.
     */
    @Override
    public void revocarAccessToken(UUID sesionId, String jti, OffsetDateTime expiraEn, String motivo) {
        jdbcTemplate.update("insert into access_token_revocado (sesion_id, jti, expira_en, motivo) values (?, ?, ?, ?) on conflict (jti) do nothing",
                sesionId, jti, expiraEn, motivo);
    }

    /**
     * Persiste auditoría trazable y deliberadamente libre de credenciales o tokens.
     */
    @Override
    public void registrarEvento(UUID usuarioId, String tipo, UUID correlacionId) {
        jdbcTemplate.update("insert into evento_autenticacion (usuario_id, tipo, correlacion_id, detalle) values (?, ?, ?, cast(? as jsonb))",
                usuarioId, tipo, correlacionId, "{}");
    }

    private List<UsuarioAutenticable> mapearUsuarioAutenticable(ResultSet resultados) throws SQLException {
        List<UsuarioAutenticable> usuarios = new ArrayList<>();
        UsuarioAutenticable usuario = null;
        List<RolUsuario> roles = new ArrayList<>();
        while (resultados.next()) {
            if (usuario == null) {
                usuario = new UsuarioAutenticable(resultados.getObject("id", UUID.class), resultados.getString("correo"),
                        resultados.getString("password_hash"), resultados.getBoolean("activo"), roles);
            }
            String rol = resultados.getString("rol_nombre");
            if (rol != null) roles.add(RolUsuario.valueOf(rol));
        }
        if (usuario != null) usuarios.add(usuario);
        return usuarios;
    }

    private List<RefreshTokenAutenticable> mapearRefresh(ResultSet resultados) throws SQLException {
        List<RefreshTokenAutenticable> refreshes = new ArrayList<>();
        RefreshTokenAutenticable refresh = null;
        List<RolUsuario> roles = new ArrayList<>();
        while (resultados.next()) {
            if (refresh == null) {
                refresh = new RefreshTokenAutenticable(resultados.getObject("refresh_id", UUID.class),
                        resultados.getObject("sesion_id", UUID.class), resultados.getObject("usuario_id", UUID.class),
                        resultados.getString("correo"), resultados.getBoolean("usuario_activo"),
                        "ACTIVA".equals(resultados.getString("sesion_estado")), resultados.getBoolean("usado"),
                        resultados.getObject("expira_en", OffsetDateTime.class), roles);
            }
            String rol = resultados.getString("rol_nombre");
            if (rol != null) roles.add(RolUsuario.valueOf(rol));
        }
        if (refresh != null) refreshes.add(refresh);
        return refreshes;
    }
}
