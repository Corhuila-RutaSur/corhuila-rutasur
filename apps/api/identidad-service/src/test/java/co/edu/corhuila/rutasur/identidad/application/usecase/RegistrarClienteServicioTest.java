package co.edu.corhuila.rutasur.identidad.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.edu.corhuila.rutasur.identidad.application.port.in.RegistrarClienteCommand;
import co.edu.corhuila.rutasur.identidad.application.port.out.CodificadorContraseniaPuerto;
import co.edu.corhuila.rutasur.identidad.application.port.out.EventoIdentidadPuertoSalida;
import co.edu.corhuila.rutasur.identidad.application.port.out.UsuarioPuertoSalida;
import co.edu.corhuila.rutasur.identidad.domain.exception.ExcepcionReglaNegocio;
import co.edu.corhuila.rutasur.identidad.domain.model.RolUsuario;
import co.edu.corhuila.rutasur.identidad.domain.model.Usuario;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Verifica las invariantes del caso de uso de registro público.
 */
@ExtendWith(MockitoExtension.class)
class RegistrarClienteServicioTest {

    @Mock
    private UsuarioPuertoSalida usuarios;
    @Mock
    private CodificadorContraseniaPuerto codificador;
    @Mock
    private EventoIdentidadPuertoSalida eventos;
    private RegistrarClienteServicio servicio;

    @BeforeEach
    void prepararServicio() {
        servicio = new RegistrarClienteServicio(usuarios, codificador, eventos);
    }

    /**
     * El registro normaliza datos, cifra la clave y entrega el rol CLIENTE.
     */
    @Test
    void registraClienteConRolClienteYEventoDeIntegracion() {
        UUID usuarioId = UUID.randomUUID();
        UUID correlacionId = UUID.randomUUID();
        when(codificador.codificar("ContraseniaSegura1!")).thenReturn("hash-bcrypt");
        when(usuarios.guardar(any(Usuario.class))).thenAnswer(invocacion -> {
            Usuario pendiente = invocacion.getArgument(0);
            return new Usuario(usuarioId, pendiente.tipoDocumento(), pendiente.numeroDocumento(), pendiente.nombres(),
                    pendiente.apellidos(), pendiente.correo(), pendiente.telefono(), pendiente.passwordHash(), true,
                    OffsetDateTime.now(), null);
        });

        Usuario registrado = servicio.registrar(comando(correlacionId));

        ArgumentCaptor<Usuario> usuario = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarios).guardar(usuario.capture());
        assertThat(registrado.id()).isEqualTo(usuarioId);
        assertThat(usuario.getValue().correo()).isEqualTo("cliente@rutasur.co");
        assertThat(usuario.getValue().numeroDocumento()).isEqualTo("1000000001");
        assertThat(usuario.getValue().passwordHash()).isEqualTo("hash-bcrypt");
        verify(usuarios).asignarRol(usuarioId, RolUsuario.CLIENTE);
        verify(eventos).registrarUsuarioCreado(usuarioId, correlacionId);
    }

    /**
     * No persiste ni publica eventos cuando el documento ya está registrado.
     */
    @Test
    void rechazaDocumentoDuplicadoAntesDePersistir() {
        when(usuarios.existeNumeroDocumento("1000000001")).thenReturn(true);

        assertThatThrownBy(() -> servicio.registrar(comando(UUID.randomUUID())))
                .isInstanceOf(ExcepcionReglaNegocio.class)
                .hasMessage("usuario.numero_documento.duplicado");

        verify(usuarios, never()).guardar(any());
        verify(eventos, never()).registrarUsuarioCreado(any(), any());
    }

    /**
     * Construye un comando válido para los escenarios del caso de uso.
     */
    private RegistrarClienteCommand comando(UUID correlacionId) {
        return new RegistrarClienteCommand("cc", "1000000001", "Ana", "Perez", "CLIENTE@RUTASUR.CO",
                "3001234567", "ContraseniaSegura1!", correlacionId);
    }
}
