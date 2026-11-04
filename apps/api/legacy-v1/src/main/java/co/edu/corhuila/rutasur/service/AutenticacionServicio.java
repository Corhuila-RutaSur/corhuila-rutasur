package co.edu.corhuila.rutasur.service;

import co.edu.corhuila.rutasur.config.security.JwtProperties;
import co.edu.corhuila.rutasur.dto.request.LoginRequest;
import co.edu.corhuila.rutasur.dto.request.RefreshTokenRequest;
import co.edu.corhuila.rutasur.dto.response.TokenResponse;
import co.edu.corhuila.rutasur.entity.Usuario;
import co.edu.corhuila.rutasur.exception.ExcepcionReglaNegocio;
import co.edu.corhuila.rutasur.repository.AutenticacionRepositorio;
import co.edu.corhuila.rutasur.repository.UsuarioRepositorio;
import co.edu.corhuila.rutasur.repository.UsuarioTipoRepositorio;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Coordina login, rotación de refresh token y cierre de sesión.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AutenticacionServicio {
    private final UsuarioRepositorio usuarios;
    private final UsuarioTipoRepositorio roles;
    private final AutenticacionRepositorio autenticacion;
    private final PasswordEncoder codificador;
    private final JwtServicio jwt;
    private final JwtProperties propiedades;

    @Transactional
    public TokenResponse login(LoginRequest solicitud) {
        log.info("evento=login_intento");
        Usuario usuario = usuarios.findByCorreoIgnoreCaseAndActivoTrue(solicitud.correo().trim()).orElseThrow(() -> new ExcepcionReglaNegocio("autenticacion.credenciales.invalidas"));
        if (!codificador.matches(solicitud.contrasenia(), usuario.getPasswordHash()))
            throw new ExcepcionReglaNegocio("autenticacion.credenciales.invalidas");
        UUID sesion = UUID.randomUUID();
        autenticacion.crearSesion(sesion, usuario.getId(), OffsetDateTime.now().plusMinutes(propiedades.refreshTokenMinutos()));
        log.info("evento=login_exitoso usuarioId={} sesionId={}", usuario.getId(), sesion);
        return emitir(usuario.getId(), sesion);
    }

    @Transactional
    public TokenResponse renovar(RefreshTokenRequest solicitud) {
        log.info("evento=refresh_intento");
        AutenticacionRepositorio.RefreshDatos datos = autenticacion.buscarRefresh(hash(solicitud.refreshToken())).orElseThrow(() -> new ExcepcionReglaNegocio("autenticacion.refresh.invalido"));
        if (datos.expiraEn().isBefore(OffsetDateTime.now()) || datos.sesionExpira().isBefore(OffsetDateTime.now())) {
            autenticacion.invalidarSesion(datos.sesionId(), "SESION_EXPIRADA");
            throw new ExcepcionReglaNegocio("autenticacion.refresh.expirado");
        }
        if (datos.usadoEn() != null || datos.revocadoEn() != null || !"ACTIVA".equals(datos.estadoSesion())) {
            autenticacion.invalidarSesion(datos.sesionId(), "ROTACION_COMPROMETIDA");
            throw new ExcepcionReglaNegocio("autenticacion.refresh.invalido");
        }
        UUID nuevo = UUID.randomUUID();
        autenticacion.marcarRefreshUsado(datos.id(), nuevo);
        log.info("evento=refresh_exitoso usuarioId={} sesionId={}", datos.usuarioId(), datos.sesionId());
        return emitir(datos.usuarioId(), datos.sesionId(), nuevo);
    }

    @Transactional
    public void logout(Long usuarioId, UUID sesionId, UUID jti, OffsetDateTime expira) {
        autenticacion.revocarAccess(jti, usuarioId, sesionId, expira, "CIERRE_SESION");
        autenticacion.invalidarSesion(sesionId, "CIERRE_SESION");
        log.info("evento=logout_exitoso usuarioId={} sesionId={}", usuarioId, sesionId);
    }

    private TokenResponse emitir(Long usuarioId, UUID sesionId) {
        return emitir(usuarioId, sesionId, UUID.randomUUID());
    }

    private TokenResponse emitir(Long usuarioId, UUID sesionId, UUID refreshId) {
        String refresh = generarAleatorio();
        autenticacion.crearRefresh(refreshId, sesionId, hash(refresh), OffsetDateTime.now().plusMinutes(propiedades.refreshTokenMinutos()));
        List<String> rolesUsuario = roles.buscarRolesActivosPorUsuarioId(usuarioId);
        String access = jwt.generar(usuarioId, sesionId, rolesUsuario, UUID.randomUUID());
        return new TokenResponse(access, refresh, "Bearer", propiedades.accessTokenMinutos() * 60, rolesUsuario);
    }

    private String generarAleatorio() {
        byte[] bytes = new byte[48];
        new java.security.SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String valor) {
        try {
            return java.util.HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(valor.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
