package co.edu.corhuila.rutasur.identidad.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.edu.corhuila.rutasur.identidad.adapter.out.security.JwtPropiedades;
import co.edu.corhuila.rutasur.identidad.application.port.in.IniciarSesionCommand;
import co.edu.corhuila.rutasur.identidad.application.port.out.AutenticacionPuertoSalida;
import co.edu.corhuila.rutasur.identidad.application.port.out.TokenAutenticacionPuertoSalida;
import co.edu.corhuila.rutasur.identidad.application.port.out.VerificadorContraseniaPuerto;
import co.edu.corhuila.rutasur.identidad.domain.exception.ExcepcionReglaNegocio;
import co.edu.corhuila.rutasur.identidad.domain.model.RolUsuario;
import co.edu.corhuila.rutasur.identidad.domain.model.SesionAutenticacion;
import co.edu.corhuila.rutasur.identidad.domain.model.SesionIniciada;
import co.edu.corhuila.rutasur.identidad.domain.model.UsuarioAutenticable;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Verifica la creación de sesión y la protección de credenciales en login.
 */
@ExtendWith(MockitoExtension.class)
class IniciarSesionServicioTest {

    @Mock
    private AutenticacionPuertoSalida autenticacion;
    @Mock
    private VerificadorContraseniaPuerto verificador;
    @Mock
    private TokenAutenticacionPuertoSalida tokens;
    private IniciarSesionServicio servicio;

    @BeforeEach
    void prepararServicio() {
        servicio = new IniciarSesionServicio(autenticacion, verificador, tokens,
                new JwtPropiedades("01234567890123456789012345678901", 15, 7));
    }

    /**
     * Emite tokens y persiste una sesión solo cuando las credenciales son correctas.
     */
    @Test
    void iniciaSesionConTokensYRefreshAlmacenadoComoHash() {
        UUID usuarioId = UUID.randomUUID();
        UUID sesionId = UUID.randomUUID();
        UUID correlacionId = UUID.randomUUID();
        UsuarioAutenticable usuario = new UsuarioAutenticable(usuarioId, "cliente@rutasur.co", "hash", true,
                List.of(RolUsuario.CLIENTE));
        when(autenticacion.buscarUsuarioAutenticablePorCorreo("cliente@rutasur.co")).thenReturn(Optional.of(usuario));
        when(verificador.coincide("ContraseniaSegura1!", "hash")).thenReturn(true);
        when(autenticacion.crearSesion(eq(usuarioId), any(), any(), any()))
                .thenReturn(new SesionAutenticacion(sesionId, usuarioId, OffsetDateTime.now().plusDays(7)));
        when(tokens.generarAccessToken(eq(usuarioId), eq(sesionId), eq(List.of(RolUsuario.CLIENTE)), any())).thenReturn("access");
        when(tokens.generarRefreshToken()).thenReturn("refresh-cliente");
        when(tokens.calcularHashRefresh("refresh-cliente")).thenReturn("hash-refresh");

        SesionIniciada resultado = servicio.iniciar(comando(correlacionId));

        assertThat(resultado.usuarioId()).isEqualTo(usuarioId);
        assertThat(resultado.tokens().accessToken()).isEqualTo("access");
        assertThat(resultado.tokens().refreshToken()).isEqualTo("refresh-cliente");
        verify(autenticacion).guardarRefreshToken(eq(sesionId), eq("hash-refresh"), any());
        verify(autenticacion).registrarEvento(usuarioId, "LOGIN_EXITOSO", correlacionId);
    }

    /**
     * Rechaza credenciales no encontradas sin crear sesión ni emitir tokens.
     */
    @Test
    void rechazaCredencialesNoEncontradasSinCrearSesion() {
        UUID correlacionId = UUID.randomUUID();
        when(autenticacion.buscarUsuarioAutenticablePorCorreo("cliente@rutasur.co")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> servicio.iniciar(comando(correlacionId)))
                .isInstanceOf(ExcepcionReglaNegocio.class)
                .hasMessage("autenticacion.credenciales.invalidas");

        verify(autenticacion, never()).crearSesion(any(), any(), any(), any());
        verify(tokens, never()).generarAccessToken(any(), any(), any(), any());
        verify(autenticacion).registrarEvento(null, "LOGIN_FALLIDO", correlacionId);
    }

    /**
     * Entrega datos válidos y trazables al caso de uso.
     */
    private IniciarSesionCommand comando(UUID correlacionId) {
        return new IniciarSesionCommand("CLIENTE@RUTASUR.CO", "ContraseniaSegura1!", "127.0.0.1", "JUnit", correlacionId);
    }
}
