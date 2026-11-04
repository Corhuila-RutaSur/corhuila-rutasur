package co.edu.corhuila.rutasur.identidad.application.port.out;

import co.edu.corhuila.rutasur.identidad.domain.model.SesionAutenticacion;
import co.edu.corhuila.rutasur.identidad.domain.model.RefreshTokenAutenticable;
import co.edu.corhuila.rutasur.identidad.domain.model.UsuarioAutenticable;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de persistencia de autenticación; encapsula sesiones y refresh tokens.
 */
public interface AutenticacionPuertoSalida {

    /**
     * Busca un usuario junto con sus roles para autenticación.
     */
    Optional<UsuarioAutenticable> buscarUsuarioAutenticablePorCorreo(String correo);

    /**
     * Crea una sesión local activa con vencimiento máximo.
     */
    SesionAutenticacion crearSesion(UUID usuarioId, String ipOrigen, String agenteUsuario, OffsetDateTime expiraEn);

    /**
     * Guarda solo el hash de un refresh token.
     */
    UUID guardarRefreshToken(UUID sesionId, String tokenHash, OffsetDateTime expiraEn);

    /**
     * Recupera y bloquea un refresh token para asegurar consumo de un único uso.
     */
    Optional<RefreshTokenAutenticable> buscarRefreshParaRotacion(String tokenHash);

    /**
     * Marca el refresh consumido y enlaza el token que lo reemplazó.
     */
    void marcarRefreshComoUsado(UUID refreshId, UUID refreshReemplazoId);

    /**
     * Invalida una sesión completa ante reutilización o cierre de sesión.
     */
    void invalidarSesion(UUID sesionId);

    /**
     * Invalida todas las sesiones vigentes de una identidad inactivada.
     */
    void invalidarSesionesUsuario(UUID usuarioId);

    /**
     * Indica si la sesión existe y permanece activa.
     */
    boolean sesionActiva(UUID sesionId);

    /**
     * Indica si el identificador de un access token fue revocado.
     */
    boolean accessTokenRevocado(String jti);

    /**
     * Revoca un access token antes de su vencimiento natural.
     */
    void revocarAccessToken(UUID sesionId, String jti, OffsetDateTime expiraEn, String motivo);

    /**
     * Registra un hecho de auditoría de autenticación sin información sensible.
     */
    void registrarEvento(UUID usuarioId, String tipo, UUID correlacionId);
}
