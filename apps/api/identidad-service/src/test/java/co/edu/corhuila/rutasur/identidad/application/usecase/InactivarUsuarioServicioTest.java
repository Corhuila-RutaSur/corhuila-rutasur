package co.edu.corhuila.rutasur.identidad.application.usecase;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.edu.corhuila.rutasur.identidad.application.port.out.AutenticacionPuertoSalida;
import co.edu.corhuila.rutasur.identidad.application.port.out.UsuarioAdministracionPuertoSalida;
import co.edu.corhuila.rutasur.identidad.domain.exception.ExcepcionReglaNegocio;
import co.edu.corhuila.rutasur.identidad.domain.model.UsuarioAdministrable;

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
 * Verifica eliminación lógica y revocación de sesiones de un usuario.
 */
@ExtendWith(MockitoExtension.class)
class InactivarUsuarioServicioTest {
    @Mock
    private UsuarioAdministracionPuertoSalida usuarios;
    @Mock
    private AutenticacionPuertoSalida autenticacion;
    private InactivarUsuarioServicio servicio;

    @BeforeEach
    void preparar() {
        servicio = new InactivarUsuarioServicio(usuarios, autenticacion);
    }

    @Test
    void inactivaUsuarioYRevocaSusSesiones() {
        UUID usuarioId = UUID.randomUUID();
        when(usuarios.buscarPorId(usuarioId)).thenReturn(Optional.of(usuario(usuarioId, true)));
        servicio.inactivar(usuarioId, UUID.randomUUID());
        verify(usuarios).inactivar(usuarioId);
        verify(autenticacion).invalidarSesionesUsuario(usuarioId);
    }

    @Test
    void rechazaUsuarioYaInactivo() {
        UUID usuarioId = UUID.randomUUID();
        when(usuarios.buscarPorId(usuarioId)).thenReturn(Optional.of(usuario(usuarioId, false)));
        assertThatThrownBy(() -> servicio.inactivar(usuarioId, UUID.randomUUID()))
                .isInstanceOf(ExcepcionReglaNegocio.class).hasMessage("usuario.inactivo");
        verify(usuarios, never()).inactivar(usuarioId);
        verify(autenticacion, never()).invalidarSesionesUsuario(usuarioId);
    }

    private UsuarioAdministrable usuario(UUID usuarioId, boolean activo) {
        return new UsuarioAdministrable(usuarioId, "CC", "100", "Ana", "Perez", "ana@rutasur.co", "3001234567", activo, OffsetDateTime.now(), List.of());
    }
}
