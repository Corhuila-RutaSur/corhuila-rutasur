package co.edu.corhuila.rutasur.identidad.application.usecase;

import co.edu.corhuila.rutasur.identidad.adapter.out.security.JwtPropiedades;
import co.edu.corhuila.rutasur.identidad.application.port.in.IniciarSesionCommand;
import co.edu.corhuila.rutasur.identidad.application.port.in.IniciarSesionUseCase;
import co.edu.corhuila.rutasur.identidad.application.port.out.AutenticacionPuertoSalida;
import co.edu.corhuila.rutasur.identidad.application.port.out.TokenAutenticacionPuertoSalida;
import co.edu.corhuila.rutasur.identidad.application.port.out.VerificadorContraseniaPuerto;
import co.edu.corhuila.rutasur.identidad.domain.exception.ExcepcionReglaNegocio;
import co.edu.corhuila.rutasur.identidad.domain.model.SesionAutenticacion;
import co.edu.corhuila.rutasur.identidad.domain.model.SesionIniciada;
import co.edu.corhuila.rutasur.identidad.domain.model.TokensAutenticacion;
import co.edu.corhuila.rutasur.identidad.domain.model.UsuarioAutenticable;

import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Orquesta la autenticación local y la emisión consistente de una sesión y sus tokens.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class IniciarSesionServicio implements IniciarSesionUseCase {
    private final AutenticacionPuertoSalida autenticacion;
    private final VerificadorContraseniaPuerto verificador;
    private final TokenAutenticacionPuertoSalida tokens;
    private final JwtPropiedades jwt;

    /**
     * Valida credenciales activas y persiste la sesión antes de entregar tokens al cliente.
     */
    @Override
    @Transactional
    public SesionIniciada iniciar(IniciarSesionCommand comando) {
        UUID correlacionId = comando.correlacionId() == null ? UUID.randomUUID() : comando.correlacionId();
        String correo = comando.correo().trim().toLowerCase(Locale.ROOT);
        log.info("correlationId={} servicio=identidad casoUso=iniciarSesion evento=inicio", correlacionId);
        UsuarioAutenticable usuario = autenticacion.buscarUsuarioAutenticablePorCorreo(correo)
                .orElseThrow(() -> credencialesInvalidas(correlacionId));
        if (!usuario.activo() || !verificador.coincide(comando.contrasenia(), usuario.passwordHash())) {
            throw credencialesInvalidas(correlacionId);
        }
        OffsetDateTime ahora = OffsetDateTime.now();
        OffsetDateTime accessExpiraEn = ahora.plusMinutes(jwt.accessMinutos());
        OffsetDateTime refreshExpiraEn = ahora.plusDays(jwt.refreshDias());
        SesionAutenticacion sesion = autenticacion.crearSesion(usuario.id(), comando.ipOrigen(), comando.agenteUsuario(), refreshExpiraEn);
        String accessToken = tokens.generarAccessToken(usuario.id(), sesion.id(), usuario.roles(), accessExpiraEn);
        String refreshToken = tokens.generarRefreshToken();
        autenticacion.guardarRefreshToken(sesion.id(), tokens.calcularHashRefresh(refreshToken), refreshExpiraEn);
        autenticacion.registrarEvento(usuario.id(), "LOGIN_EXITOSO", correlacionId);
        log.info("correlationId={} servicio=identidad casoUso=iniciarSesion evento=exito usuarioId={} sesionId={}",
                correlacionId, usuario.id(), sesion.id());
        return new SesionIniciada(usuario.id(), usuario.correo(), usuario.roles(),
                new TokensAutenticacion(accessToken, refreshToken, accessExpiraEn, refreshExpiraEn));
    }

    private ExcepcionReglaNegocio credencialesInvalidas(UUID correlacionId) {
        autenticacion.registrarEvento(null, "LOGIN_FALLIDO", correlacionId);
        log.warn("correlationId={} servicio=identidad casoUso=iniciarSesion evento=credenciales_invalidas", correlacionId);
        return new ExcepcionReglaNegocio("autenticacion.credenciales.invalidas");
    }
}
