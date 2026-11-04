package co.edu.corhuila.rutasur.identidad.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

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
 * Verifica consultas administrativas sin filtrar datos sensibles.
 */
@ExtendWith(MockitoExtension.class)
class ConsultarUsuariosAdministracionServicioTest {
    @Mock
    private UsuarioAdministracionPuertoSalida usuarios;
    private ConsultarUsuariosAdministracionServicio servicio;

    @BeforeEach
    void preparar() {
        servicio = new ConsultarUsuariosAdministracionServicio(usuarios);
    }

    /**
     * Devuelve la lista proyectada que entrega el adaptador de administración.
     */
    @Test
    void listaUsuariosAdministrables() {
        UsuarioAdministrable usuario = usuario();
        when(usuarios.listar()).thenReturn(List.of(usuario));
        assertThat(servicio.listar()).containsExactly(usuario);
    }

    /**
     * Reporta una regla funcional cuando el administrador consulta una identidad inexistente.
     */
    @Test
    void rechazaConsultaDeUsuarioInexistente() {
        UUID usuarioId = UUID.randomUUID();
        when(usuarios.buscarPorId(usuarioId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> servicio.consultar(usuarioId)).isInstanceOf(ExcepcionReglaNegocio.class).hasMessage("usuario.no_encontrado");
    }

    private UsuarioAdministrable usuario() {
        return new UsuarioAdministrable(UUID.randomUUID(), "CC", "100", "Ana", "Perez", "ana@rutasur.co", "3001234567", true, OffsetDateTime.now(), List.of());
    }
}
