package co.edu.corhuila.rutasur.identidad.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.edu.corhuila.rutasur.identidad.adapter.out.security.JwtPropiedades;
import co.edu.corhuila.rutasur.identidad.application.port.in.RenovarSesionCommand;
import co.edu.corhuila.rutasur.identidad.application.port.out.AutenticacionPuertoSalida;
import co.edu.corhuila.rutasur.identidad.application.port.out.TokenAutenticacionPuertoSalida;
import co.edu.corhuila.rutasur.identidad.domain.exception.ExcepcionReglaNegocio;
import co.edu.corhuila.rutasur.identidad.domain.model.RefreshTokenAutenticable;
import co.edu.corhuila.rutasur.identidad.domain.model.RolUsuario;
import co.edu.corhuila.rutasur.identidad.domain.model.SesionIniciada;

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
 * Verifica rotación de refresh y defensa ante reutilización de token.
 */
@ExtendWith(MockitoExtension.class)
class RenovarSesionServicioTest {
    @Mock
    private AutenticacionPuertoSalida autenticacion;
    @Mock
    private TokenAutenticacionPuertoSalida tokens;
    private RenovarSesionServicio servicio;

    @BeforeEach
    void prepararServicio() {
        servicio = new RenovarSesionServicio(autenticacion, tokens,
                new JwtPropiedades("01234567890123456789012345678901", 15, 7));
    }

    /**
     * Rota el refresh, conserva vencimiento máximo y emite un access nuevo.
     */
    @Test
    void rotaRefreshValidoUnaSolaVez() {
        UUID refreshId = UUID.randomUUID();
        UUID nuevoRefreshId = UUID.randomUUID();
        UUID sesionId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();
        UUID correlacionId = UUID.randomUUID();
        OffsetDateTime expiraEn = OffsetDateTime.now().plusDays(2);
        RefreshTokenAutenticable refresh = new RefreshTokenAutenticable(refreshId, sesionId, usuarioId,
                "cliente@rutasur.co", true, true, false, expiraEn, List.of(RolUsuario.CLIENTE));
        when(tokens.calcularHashRefresh("refresh-anterior")).thenReturn("hash-anterior");
        when(autenticacion.buscarRefreshParaRotacion("hash-anterior")).thenReturn(Optional.of(refresh));
        when(tokens.generarRefreshToken()).thenReturn("refresh-nuevo");
        when(tokens.calcularHashRefresh("refresh-nuevo")).thenReturn("hash-nuevo");
        when(autenticacion.guardarRefreshToken(sesionId, "hash-nuevo", expiraEn)).thenReturn(nuevoRefreshId);
        when(tokens.generarAccessToken(eq(usuarioId), eq(sesionId), eq(List.of(RolUsuario.CLIENTE)), any())).thenReturn("access-nuevo");

        SesionIniciada resultado = servicio.renovar(new RenovarSesionCommand("refresh-anterior", correlacionId));

        assertThat(resultado.tokens().accessToken()).isEqualTo("access-nuevo");
        assertThat(resultado.tokens().refreshToken()).isEqualTo("refresh-nuevo");
        assertThat(resultado.tokens().refreshExpiraEn()).isEqualTo(expiraEn);
        verify(autenticacion).marcarRefreshComoUsado(refreshId, nuevoRefreshId);
        verify(autenticacion).registrarEvento(usuarioId, "REFRESH_ROTADO", correlacionId);
    }

    /**
     * Un refresh ya consumido invalida la sesión completa en vez de renovarla.
     */
    @Test
    void invalidaSesionCuandoSeReutilizaRefresh() {
        UUID sesionId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();
        UUID correlacionId = UUID.randomUUID();
        RefreshTokenAutenticable refresh = new RefreshTokenAutenticable(UUID.randomUUID(), sesionId, usuarioId,
                "cliente@rutasur.co", true, true, true, OffsetDateTime.now().plusDays(2), List.of(RolUsuario.CLIENTE));
        when(tokens.calcularHashRefresh("refresh-reutilizado")).thenReturn("hash-reutilizado");
        when(autenticacion.buscarRefreshParaRotacion("hash-reutilizado")).thenReturn(Optional.of(refresh));

        assertThatThrownBy(() -> servicio.renovar(new RenovarSesionCommand("refresh-reutilizado", correlacionId)))
                .isInstanceOf(ExcepcionReglaNegocio.class)
                .hasMessage("autenticacion.refresh.invalido");

        verify(autenticacion).invalidarSesion(sesionId);
        verify(autenticacion).registrarEvento(usuarioId, "REFRESH_REUTILIZADO", correlacionId);
        verify(tokens, never()).generarAccessToken(any(), any(), any(), any());
    }
}
