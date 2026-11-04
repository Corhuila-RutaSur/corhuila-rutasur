package co.edu.corhuila.rutasur.identidad.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.edu.corhuila.rutasur.identidad.application.port.in.ActualizarUsuarioAdministrativoCommand;
import co.edu.corhuila.rutasur.identidad.application.port.out.RolPuertoSalida;
import co.edu.corhuila.rutasur.identidad.application.port.out.UsuarioAdministracionPuertoSalida;
import co.edu.corhuila.rutasur.identidad.application.port.out.UsuarioPuertoSalida;
import co.edu.corhuila.rutasur.identidad.domain.exception.ExcepcionReglaNegocio;
import co.edu.corhuila.rutasur.identidad.domain.model.RolUsuario;
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
 * Verifica validaciones y sustitución transaccional de roles administrativos.
 */
@ExtendWith(MockitoExtension.class)
class ActualizarUsuarioAdministrativoServicioTest {
    @Mock
    private UsuarioAdministracionPuertoSalida administracion;
    @Mock
    private UsuarioPuertoSalida usuarios;
    @Mock
    private RolPuertoSalida roles;
    private ActualizarUsuarioAdministrativoServicio servicio;

    @BeforeEach
    void preparar() {
        servicio = new ActualizarUsuarioAdministrativoServicio(administracion, usuarios, roles);
    }

    /**
     * Actualiza datos y reemplaza roles cuando la selección existe y está activa.
     */
    @Test
    void actualizaUsuarioYRolesActivos() {
        UsuarioAdministrable actual = usuario("ana@rutasur.co", "3001234567");
        List<RolUsuario> nuevosRoles = List.of(RolUsuario.GUIA, RolUsuario.CONDUCTOR);
        when(administracion.buscarPorId(actual.id())).thenReturn(Optional.of(actual), Optional.of(actual));
        when(roles.todosActivos(nuevosRoles)).thenReturn(true);

        UsuarioAdministrable resultado = servicio.actualizar(new ActualizarUsuarioAdministrativoCommand(actual.id(), "Ana Maria", "Perez", actual.correo(), actual.telefono(), nuevosRoles, UUID.randomUUID()));

        assertThat(resultado).isEqualTo(actual);
        verify(administracion).actualizar(actual.id(), "Ana Maria", "Perez", actual.correo(), actual.telefono());
        verify(administracion).reemplazarRoles(actual.id(), nuevosRoles);
    }

    /**
     * Rechaza correo ya usado por otra identidad antes de cambiar roles o datos.
     */
    @Test
    void rechazaCorreoDuplicadoCuandoCambia() {
        UsuarioAdministrable actual = usuario("ana@rutasur.co", "3001234567");
        when(administracion.buscarPorId(actual.id())).thenReturn(Optional.of(actual));
        when(usuarios.existeCorreo("otro@rutasur.co")).thenReturn(true);
        var comando = new ActualizarUsuarioAdministrativoCommand(actual.id(), "Ana", "Perez", "otro@rutasur.co", actual.telefono(), List.of(RolUsuario.GUIA), UUID.randomUUID());

        assertThatThrownBy(() -> servicio.actualizar(comando)).isInstanceOf(ExcepcionReglaNegocio.class).hasMessage("usuario.correo.duplicado");
        verify(administracion, never()).reemplazarRoles(actual.id(), comando.roles());
    }

    private UsuarioAdministrable usuario(String correo, String telefono) {
        return new UsuarioAdministrable(UUID.randomUUID(), "CC", "100", "Ana", "Perez", correo, telefono, true, OffsetDateTime.now(), List.of(RolUsuario.CLIENTE));
    }
}
