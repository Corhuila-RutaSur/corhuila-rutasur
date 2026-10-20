package co.edu.corhuila.rutasur.repository;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Acceso SQL encapsulado para sesiones, refresh tokens y revocación.
 */
@Repository
@RequiredArgsConstructor
public class AutenticacionRepositorio {
    private final JdbcTemplate jdbc;

    public void crearSesion(UUID id, Long usuarioId, OffsetDateTime expira) {
        jdbc.update("insert into sesion_autenticacion (id, usuario_id, expira_en) values (?, ?, ?)", id, usuarioId, expira);
    }

    public void crearRefresh(UUID id, UUID sesionId, String hash, OffsetDateTime expira) {
        jdbc.update("insert into refresh_token (id, sesion_id, token_hash, expira_en) values (?, ?, ?, ?)", id, sesionId, hash, expira);
    }

    public Optional<RefreshDatos> buscarRefresh(String hash) {
        return jdbc.query("select r.id, r.sesion_id, r.usado_en, r.revocado_en, r.expira_en, s.usuario_id, s.estado::text estado, s.expira_en sesion_expira from refresh_token r join sesion_autenticacion s on s.id=r.sesion_id where r.token_hash=?", rs -> rs.next() ? Optional.of(new RefreshDatos(UUID.fromString(rs.getString("id")), UUID.fromString(rs.getString("sesion_id")), rs.getObject("usado_en", OffsetDateTime.class), rs.getObject("revocado_en", OffsetDateTime.class), rs.getObject("expira_en", OffsetDateTime.class), rs.getLong("usuario_id"), rs.getString("estado"), rs.getObject("sesion_expira", OffsetDateTime.class))) : Optional.empty(), hash);
    }

    public void marcarRefreshUsado(UUID id, UUID reemplazo) {
        jdbc.update("update refresh_token set usado_en=current_timestamp, reemplazado_por_id=? where id=?", reemplazo, id);
    }

    public void invalidarSesion(UUID id, String motivo) {
        jdbc.update("update sesion_autenticacion set estado=cast('INVALIDADA' as estado_sesion), invalidada_en=current_timestamp, motivo_invalidacion=cast(? as motivo_invalidacion_sesion) where id=?", motivo, id);
    }

    public boolean sesionActiva(UUID id, Long usuarioId) {
        return Boolean.TRUE.equals(jdbc.queryForObject("select exists(select 1 from sesion_autenticacion where id=? and usuario_id=? and estado=cast('ACTIVA' as estado_sesion) and expira_en>current_timestamp)", Boolean.class, id, usuarioId));
    }

    public boolean jtiRevocado(UUID jti) {
        return Boolean.TRUE.equals(jdbc.queryForObject("select exists(select 1 from access_token_revocado where jti=?)", Boolean.class, jti));
    }

    public void revocarAccess(UUID jti, Long usuarioId, UUID sesionId, OffsetDateTime expira, String motivo) {
        jdbc.update("insert into access_token_revocado (jti, usuario_id, sesion_id, expira_en, motivo) values (?, ?, ?, ?, ?) on conflict (jti) do nothing", jti, usuarioId, sesionId, expira, motivo);
    }

    public record RefreshDatos(UUID id, UUID sesionId, OffsetDateTime usadoEn, OffsetDateTime revocadoEn,
                               OffsetDateTime expiraEn, Long usuarioId, String estadoSesion,
                               OffsetDateTime sesionExpira) {
    }
}
