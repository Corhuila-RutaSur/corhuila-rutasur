package co.edu.corhuila.rutasur.identidad.application.usecase;

import co.edu.corhuila.rutasur.identidad.application.port.in.CrearUsuarioAdministrativoCommand;
import co.edu.corhuila.rutasur.identidad.application.port.in.CrearUsuarioAdministrativoUseCase;
import co.edu.corhuila.rutasur.identidad.application.port.out.CodificadorContraseniaPuerto;
import co.edu.corhuila.rutasur.identidad.application.port.out.RolPuertoSalida;
import co.edu.corhuila.rutasur.identidad.application.port.out.UsuarioPuertoSalida;
import co.edu.corhuila.rutasur.identidad.domain.exception.ExcepcionReglaNegocio;
import co.edu.corhuila.rutasur.identidad.domain.model.Usuario;

import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Crea usuarios internos con roles válidos sin alterar las reglas de registro público.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CrearUsuarioAdministrativoServicio implements CrearUsuarioAdministrativoUseCase {
    private final UsuarioPuertoSalida usuarios;
    private final RolPuertoSalida roles;
    private final CodificadorContraseniaPuerto codificador;

    /**
     * Persiste usuario y sus roles activos dentro de una única transacción ACID.
     */
    @Override
    @Transactional
    public Usuario crear(CrearUsuarioAdministrativoCommand comando) {
        UUID correlacionId = comando.correlacionId() == null ? UUID.randomUUID() : comando.correlacionId();
        String documento = comando.numeroDocumento().trim().toUpperCase(Locale.ROOT);
        String correo = comando.correo().trim().toLowerCase(Locale.ROOT);
        String telefono = comando.telefono().trim();
        if (usuarios.existeNumeroDocumento(documento))
            throw new ExcepcionReglaNegocio("usuario.numero_documento.duplicado");
        if (usuarios.existeCorreo(correo)) throw new ExcepcionReglaNegocio("usuario.correo.duplicado");
        if (usuarios.existeTelefono(telefono)) throw new ExcepcionReglaNegocio("usuario.telefono.duplicado");
        if (!roles.todosActivos(comando.roles())) throw new ExcepcionReglaNegocio("usuario.roles.invalidos");
        Usuario creado = usuarios.guardar(new Usuario(null, comando.tipoDocumento().trim().toUpperCase(Locale.ROOT), documento,
                comando.nombres().trim(), comando.apellidos().trim(), correo, telefono, codificador.codificar(comando.contrasenia()), true,
                OffsetDateTime.now(), null));
        comando.roles().stream().distinct().forEach(rol -> usuarios.asignarRol(creado.id(), rol));
        log.info("correlationId={} servicio=identidad casoUso=crearUsuarioAdministrativo evento=exito usuarioId={}", correlacionId, creado.id());
        return creado;
    }
}
