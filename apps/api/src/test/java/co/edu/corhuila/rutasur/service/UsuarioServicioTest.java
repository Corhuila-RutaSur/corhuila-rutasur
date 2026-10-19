package co.edu.corhuila.rutasur.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.edu.corhuila.rutasur.dto.request.UsuarioRegistroRequest;
import co.edu.corhuila.rutasur.dto.response.UsuarioResponse;
import co.edu.corhuila.rutasur.entity.Usuario;
import co.edu.corhuila.rutasur.exception.ExcepcionReglaNegocio;
import co.edu.corhuila.rutasur.mapper.UsuarioMapper;
import co.edu.corhuila.rutasur.repository.UsuarioRepositorio;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Prueba las reglas de negocio principales del caso de uso de usuarios.
 */
@ExtendWith(MockitoExtension.class)
class UsuarioServicioTest {

    @Mock
    private UsuarioRepositorio usuarioRepositorio;

    @Mock
    private UsuarioMapper usuarioMapper;

    @Mock
    private PasswordEncoder codificadorContrasenias;

    @InjectMocks
    private UsuarioServicio usuarioServicio;

    /**
     * Verifica que el registro codifique la contrasenia antes de persistir el usuario.
     */
    @Test
    void registrar_cuandoDatosSonUnicos_codificaContraseniaYPersisteUsuario() {
        UsuarioRegistroRequest solicitud = solicitudValida();
        Usuario usuario = usuarioValido();
        UsuarioResponse respuesta = respuestaValida();
        when(usuarioMapper.aEntidad(solicitud)).thenReturn(usuario);
        when(codificadorContrasenias.encode(solicitud.contrasenia())).thenReturn("hash-bcrypt");
        when(usuarioRepositorio.save(usuario)).thenReturn(usuario);
        when(usuarioMapper.aRespuesta(usuario)).thenReturn(respuesta);

        UsuarioResponse resultado = usuarioServicio.registrar(solicitud);

        assertEquals(respuesta, resultado);
        assertEquals("hash-bcrypt", usuario.getPasswordHash());
        assertEquals("ana@rutasur.co", usuario.getCorreo());
        verify(usuarioRepositorio).save(usuario);
    }

    /**
     * Verifica que no se permita repetir un numero de documento.
     */
    @Test
    void registrar_cuandoDocumentoYaExiste_lanzaReglaDeNegocio() {
        UsuarioRegistroRequest solicitud = solicitudValida();
        Usuario usuario = usuarioValido();
        when(usuarioMapper.aEntidad(solicitud)).thenReturn(usuario);
        when(usuarioRepositorio.existsByNumeroDocumento("123456789")).thenReturn(true);

        ExcepcionReglaNegocio excepcion = assertThrows(
                ExcepcionReglaNegocio.class, () -> usuarioServicio.registrar(solicitud));
        assertEquals("usuario.numero_documento.duplicado", excepcion.getCodigo());
    }

    /**
     * Verifica que la eliminacion logica use una actualizacion puntual de persistencia.
     */
    @Test
    void eliminarLogicamente_cuandoUsuarioEstaActivo_desactivaSinEliminarFila() {
        when(usuarioRepositorio.desactivarPorId(eq(5L), any(OffsetDateTime.class))).thenReturn(1);

        usuarioServicio.eliminarLogicamente(5L);

        verify(usuarioRepositorio).desactivarPorId(eq(5L), any(OffsetDateTime.class));
    }

    /**
     * Construye una solicitud valida para las pruebas.
     *
     * @return solicitud de registro de prueba.
     */
    private UsuarioRegistroRequest solicitudValida() {
        return new UsuarioRegistroRequest(
                "cc", "123456789", "Ana", "Perez", "ANA@RUTASUR.CO", "3001234567", "ClaveSegura1");
    }

    /**
     * Construye una entidad con datos no normalizados para validar la normalizacion.
     *
     * @return usuario de prueba.
     */
    private Usuario usuarioValido() {
        Usuario usuario = new Usuario();
        usuario.setTipoDocumento("cc");
        usuario.setNumeroDocumento("123456789");
        usuario.setNombres("Ana");
        usuario.setApellidos("Perez");
        usuario.setCorreo("ANA@RUTASUR.CO");
        usuario.setTelefono("3001234567");
        return usuario;
    }

    /**
     * Construye una respuesta segura esperada.
     *
     * @return respuesta de prueba.
     */
    private UsuarioResponse respuestaValida() {
        return new UsuarioResponse(
                1L, "CC", "123456789", "Ana", "Perez", "ana@rutasur.co", "3001234567", true,
                OffsetDateTime.now(), null);
    }
}
