package co.edu.corhuila.rutasur.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.edu.corhuila.rutasur.config.security.JwtProperties;
import co.edu.corhuila.rutasur.dto.request.LoginRequest;
import co.edu.corhuila.rutasur.dto.response.TokenResponse;
import co.edu.corhuila.rutasur.entity.Usuario;
import co.edu.corhuila.rutasur.exception.ExcepcionReglaNegocio;
import co.edu.corhuila.rutasur.repository.AutenticacionRepositorio;
import co.edu.corhuila.rutasur.repository.UsuarioRepositorio;
import co.edu.corhuila.rutasur.repository.UsuarioTipoRepositorio;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

/** Prueba el inicio de sesión sin depender de una base de datos ni de tokens reales. */
@ExtendWith(MockitoExtension.class)
class AutenticacionServicioTest {

    @Mock private UsuarioRepositorio usuarioRepositorio;
    @Mock private UsuarioTipoRepositorio usuarioTipoRepositorio;
    @Mock private AutenticacionRepositorio autenticacionRepositorio;
    @Mock private PasswordEncoder codificadorContrasenias;
    @Mock private JwtServicio jwtServicio;
    @Mock private JwtProperties jwtProperties;
    @InjectMocks private AutenticacionServicio autenticacionServicio;

    /** Verifica que credenciales correctas creen sesión, refresh token y access token. */
    @Test
    void login_cuandoCredencialesSonCorrectas_emiteTokensYRegistraSesion() {
        Usuario usuario = new Usuario();
        usuario.setId(8L);
        usuario.setPasswordHash("hash-bcrypt");
        when(usuarioRepositorio.findByCorreoIgnoreCaseAndActivoTrue("ana@rutasur.co"))
                .thenReturn(Optional.of(usuario));
        when(codificadorContrasenias.matches("ClaveSegura1", "hash-bcrypt")).thenReturn(true);
        when(usuarioTipoRepositorio.buscarRolesActivosPorUsuarioId(8L)).thenReturn(List.of("CLIENTE"));
        when(jwtProperties.refreshTokenMinutos()).thenReturn(120L);
        when(jwtProperties.accessTokenMinutos()).thenReturn(15L);
        when(jwtServicio.generar(anyLong(), any(UUID.class), any(List.class), any(UUID.class))).thenReturn("access-token");

        TokenResponse respuesta = autenticacionServicio.login(new LoginRequest("ana@rutasur.co", "ClaveSegura1"));

        assertEquals("access-token", respuesta.accessToken());
        assertEquals("Bearer", respuesta.tipoToken());
        assertEquals(List.of("CLIENTE"), respuesta.roles());
        verify(autenticacionRepositorio).crearSesion(any(UUID.class), anyLong(), any());
        verify(autenticacionRepositorio).crearRefresh(any(UUID.class), any(UUID.class), anyString(), any());
    }

    /** Verifica que una contraseña incorrecta no cree sesión ni tokens. */
    @Test
    void login_cuandoContraseniaEsIncorrecta_rechazaSinCrearSesion() {
        Usuario usuario = new Usuario();
        usuario.setPasswordHash("hash-bcrypt");
        when(usuarioRepositorio.findByCorreoIgnoreCaseAndActivoTrue("ana@rutasur.co"))
                .thenReturn(Optional.of(usuario));
        when(codificadorContrasenias.matches("incorrecta", "hash-bcrypt")).thenReturn(false);

        ExcepcionReglaNegocio excepcion = assertThrows(ExcepcionReglaNegocio.class,
                () -> autenticacionServicio.login(new LoginRequest("ana@rutasur.co", "incorrecta")));

        assertEquals("autenticacion.credenciales.invalidas", excepcion.getCodigo());
        verify(autenticacionRepositorio, never()).crearSesion(any(), anyLong(), any());
    }
}
