package co.edu.corhuila.rutasur.identidad.application.usecase;

import co.edu.corhuila.rutasur.identidad.application.port.in.RegistrarClienteCommand;
import co.edu.corhuila.rutasur.identidad.application.port.in.RegistrarClienteUseCase;
import co.edu.corhuila.rutasur.identidad.application.port.out.CodificadorContraseniaPuerto;
import co.edu.corhuila.rutasur.identidad.application.port.out.EventoIdentidadPuertoSalida;
import co.edu.corhuila.rutasur.identidad.application.port.out.UsuarioPuertoSalida;
import co.edu.corhuila.rutasur.identidad.domain.exception.ExcepcionReglaNegocio;
import co.edu.corhuila.rutasur.identidad.domain.model.RolUsuario;
import co.edu.corhuila.rutasur.identidad.domain.model.Usuario;

import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Orquesta el registro público y conserva las invariantes de identidad.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RegistrarClienteServicio implements RegistrarClienteUseCase {
    private final UsuarioPuertoSalida usuarios;
    private final CodificadorContraseniaPuerto codificador;
    private final EventoIdentidadPuertoSalida eventos;

    /**
     * Registra un cliente, asigna su rol y prepara su evento de integración.
     */
    @Override
    @Transactional
    public Usuario registrar(RegistrarClienteCommand comando) {
        UUID correlacionId = comando.correlacionId() == null ? UUID.randomUUID() : comando.correlacionId();
        String documento = normalizarMayusculas(comando.numeroDocumento());
        String correo = comando.correo().trim().toLowerCase(Locale.ROOT);
        String telefono = comando.telefono().trim();
        log.info("correlationId={} servicio=identidad casoUso=registrarCliente evento=inicio", correlacionId);
        validarUnicidad(documento, correo, telefono);
        Usuario creado = usuarios.guardar(new Usuario(null, normalizarMayusculas(comando.tipoDocumento()), documento,
                comando.nombres().trim(), comando.apellidos().trim(), correo, telefono,
                codificador.codificar(comando.contrasenia()), true, OffsetDateTime.now(), null));
        usuarios.asignarRol(creado.id(), RolUsuario.CLIENTE);
        eventos.registrarUsuarioCreado(creado.id(), correlacionId);
        log.info("correlationId={} servicio=identidad casoUso=registrarCliente evento=exito agregadoId={}", correlacionId, creado.id());
        return creado;
    }

    private void validarUnicidad(String documento, String correo, String telefono) {
        if (usuarios.existeNumeroDocumento(documento))
            throw new ExcepcionReglaNegocio("usuario.numero_documento.duplicado");
        if (usuarios.existeCorreo(correo)) throw new ExcepcionReglaNegocio("usuario.correo.duplicado");
        if (usuarios.existeTelefono(telefono)) throw new ExcepcionReglaNegocio("usuario.telefono.duplicado");
    }

    private String normalizarMayusculas(String valor) {
        return valor.trim().toUpperCase(Locale.ROOT);
    }
}
