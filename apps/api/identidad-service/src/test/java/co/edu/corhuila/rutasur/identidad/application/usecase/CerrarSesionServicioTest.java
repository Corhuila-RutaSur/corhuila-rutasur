package co.edu.corhuila.rutasur.identidad.application.usecase;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.edu.corhuila.rutasur.identidad.application.port.in.CerrarSesionCommand;
import co.edu.corhuila.rutasur.identidad.application.port.out.AutenticacionPuertoSalida;
import co.edu.corhuila.rutasur.identidad.application.port.out.TokenAutenticacionPuertoSalida;
import co.edu.corhuila.rutasur.identidad.domain.exception.ExcepcionAutenticacion;
import co.edu.corhuila.rutasur.identidad.domain.model.TokenAccesoValidado;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Verifica revocación de access token e invalidación de sesión al cerrar sesión.
 */
@ExtendWith(MockitoExtension.class)
class CerrarSesionServicioTest {
    @Mock
    private TokenAutenticacionPuertoSalida tokens;
    @Mock
    private AutenticacionPuertoSalida autenticacion;
    private CerrarSesionServicio servicio;

    @BeforeEach
    void prepararServicio() {
        servicio = new CerrarSesionServicio(tokens, autenticacion);
    }

    /**
     * Revoca el jti actual, invalida la sesión y deja auditoría de cierre.
     */
    @Test
    void cierraSesionVigente() {
        UUID usuarioId = UUID.randomUUID();
        UUID sesionId = UUID.randomUUID();
        UUID correlacionId = UUID.randomUUID();
        TokenAccesoValidado token = new TokenAccesoValidado(usuarioId, sesionId, "jti-actual", OffsetDateTime.now().plusMinutes(10), List.of());
        when(tokens.validarAccessToken("access")).thenReturn(token);
        when(autenticacion.sesionActiva(sesionId)).thenReturn(true);
        when(autenticacion.accessTokenRevocado("jti-actual")).thenReturn(false);

        servicio.cerrar(new CerrarSesionCommand("access", correlacionId));

        verify(autenticacion).revocarAccessToken(sesionId, "jti-actual", token.expiraEn(), "LOGOUT");
        verify(autenticacion).invalidarSesion(sesionId);
        verify(autenticacion).registrarEvento(usuarioId, "LOGOUT", correlacionId);
    }

    /**
     * No ejecuta revocaciones cuando la sesión ya no está vigente.
     */
    @Test
    void rechazaTokenDeSesionInactiva() {
        UUID sesionId = UUID.randomUUID();
        TokenAccesoValidado token = new TokenAccesoValidado(UUID.randomUUID(), sesionId, "jti", OffsetDateTime.now().plusMinutes(10), List.of());
        when(tokens.validarAccessToken("access")).thenReturn(token);
        when(autenticacion.sesionActiva(sesionId)).thenReturn(false);

        assertThatThrownBy(() -> servicio.cerrar(new CerrarSesionCommand("access", UUID.randomUUID())))
                .isInstanceOf(ExcepcionAutenticacion.class)
                .hasMessage("autenticacion.token.invalido");

        verify(autenticacion, never()).revocarAccessToken(sesionId, "jti", token.expiraEn(), "LOGOUT");
    }
}
