package co.edu.corhuila.rutasur.identidad.application.usecase;

import co.edu.corhuila.rutasur.identidad.application.port.in.CerrarSesionCommand;
import co.edu.corhuila.rutasur.identidad.application.port.in.CerrarSesionUseCase;
import co.edu.corhuila.rutasur.identidad.application.port.out.AutenticacionPuertoSalida;
import co.edu.corhuila.rutasur.identidad.application.port.out.TokenAutenticacionPuertoSalida;
import co.edu.corhuila.rutasur.identidad.domain.exception.ExcepcionAutenticacion;
import co.edu.corhuila.rutasur.identidad.domain.model.TokenAccesoValidado;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Cierra una sesión mediante revocación explícita e invalidación lógica.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CerrarSesionServicio implements CerrarSesionUseCase {
    private final TokenAutenticacionPuertoSalida tokens;
    private final AutenticacionPuertoSalida autenticacion;

    /**
     * Revoca el JWT recibido y evita toda renovación futura de su sesión.
     */
    @Override
    @Transactional
    public void cerrar(CerrarSesionCommand comando) {
        UUID correlacionId = comando.correlacionId() == null ? UUID.randomUUID() : comando.correlacionId();
        TokenAccesoValidado token = tokens.validarAccessToken(comando.accessToken());
        if (!autenticacion.sesionActiva(token.sesionId()) || autenticacion.accessTokenRevocado(token.jti())) {
            throw new ExcepcionAutenticacion("autenticacion.token.invalido");
        }
        autenticacion.revocarAccessToken(token.sesionId(), token.jti(), token.expiraEn(), "LOGOUT");
        autenticacion.invalidarSesion(token.sesionId());
        autenticacion.registrarEvento(token.usuarioId(), "LOGOUT", correlacionId);
        log.info("correlationId={} servicio=identidad casoUso=cerrarSesion evento=exito usuarioId={} sesionId={}",
                correlacionId, token.usuarioId(), token.sesionId());
    }
}
