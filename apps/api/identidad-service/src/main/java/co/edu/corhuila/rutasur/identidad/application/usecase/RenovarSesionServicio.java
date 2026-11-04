package co.edu.corhuila.rutasur.identidad.application.usecase;

import co.edu.corhuila.rutasur.identidad.adapter.out.security.JwtPropiedades;
import co.edu.corhuila.rutasur.identidad.application.port.in.RenovarSesionCommand;
import co.edu.corhuila.rutasur.identidad.application.port.in.RenovarSesionUseCase;
import co.edu.corhuila.rutasur.identidad.application.port.out.AutenticacionPuertoSalida;
import co.edu.corhuila.rutasur.identidad.application.port.out.TokenAutenticacionPuertoSalida;
import co.edu.corhuila.rutasur.identidad.domain.exception.ExcepcionReglaNegocio;
import co.edu.corhuila.rutasur.identidad.domain.model.RefreshTokenAutenticable;
import co.edu.corhuila.rutasur.identidad.domain.model.SesionIniciada;
import co.edu.corhuila.rutasur.identidad.domain.model.TokensAutenticacion;

import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Rota refresh tokens y protege la sesión ante reuso o vencimiento.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RenovarSesionServicio implements RenovarSesionUseCase {
    private final AutenticacionPuertoSalida autenticacion;
    private final TokenAutenticacionPuertoSalida tokens;
    private final JwtPropiedades jwt;

    /**
     * Consume un refresh una sola vez y entrega una nueva pareja de tokens.
     */
    @Override
    @Transactional
    public SesionIniciada renovar(RenovarSesionCommand comando) {
        UUID correlacionId = comando.correlacionId() == null ? UUID.randomUUID() : comando.correlacionId();
        log.info("correlationId={} servicio=identidad casoUso=renovarSesion evento=inicio", correlacionId);
        RefreshTokenAutenticable refresh = autenticacion.buscarRefreshParaRotacion(tokens.calcularHashRefresh(comando.refreshToken()))
                .orElseThrow(() -> refreshInvalido(correlacionId));
        if (refresh.usado()) {
            autenticacion.invalidarSesion(refresh.sesionId());
            autenticacion.registrarEvento(refresh.usuarioId(), "REFRESH_REUTILIZADO", correlacionId);
            throw refreshInvalido(correlacionId);
        }
        if (!refresh.usuarioActivo() || !refresh.sesionActiva() || !refresh.expiraEn().isAfter(OffsetDateTime.now())) {
            autenticacion.invalidarSesion(refresh.sesionId());
            autenticacion.registrarEvento(refresh.usuarioId(), "REFRESH_INVALIDO", correlacionId);
            throw refreshInvalido(correlacionId);
        }
        OffsetDateTime accessExpiraEn = OffsetDateTime.now().plusMinutes(jwt.accessMinutos());
        String nuevoRefresh = tokens.generarRefreshToken();
        UUID nuevoRefreshId = autenticacion.guardarRefreshToken(refresh.sesionId(), tokens.calcularHashRefresh(nuevoRefresh), refresh.expiraEn());
        autenticacion.marcarRefreshComoUsado(refresh.id(), nuevoRefreshId);
        String access = tokens.generarAccessToken(refresh.usuarioId(), refresh.sesionId(), refresh.roles(), accessExpiraEn);
        autenticacion.registrarEvento(refresh.usuarioId(), "REFRESH_ROTADO", correlacionId);
        log.info("correlationId={} servicio=identidad casoUso=renovarSesion evento=exito usuarioId={} sesionId={}",
                correlacionId, refresh.usuarioId(), refresh.sesionId());
        return new SesionIniciada(refresh.usuarioId(), refresh.correo(), refresh.roles(),
                new TokensAutenticacion(access, nuevoRefresh, accessExpiraEn, refresh.expiraEn()));
    }

    private ExcepcionReglaNegocio refreshInvalido(UUID correlacionId) {
        log.warn("correlationId={} servicio=identidad casoUso=renovarSesion evento=refresh_invalido", correlacionId);
        return new ExcepcionReglaNegocio("autenticacion.refresh.invalido");
    }
}
