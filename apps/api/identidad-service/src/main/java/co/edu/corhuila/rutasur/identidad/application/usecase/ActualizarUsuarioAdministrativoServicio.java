package co.edu.corhuila.rutasur.identidad.application.usecase;

import co.edu.corhuila.rutasur.identidad.application.port.in.ActualizarUsuarioAdministrativoCommand;
import co.edu.corhuila.rutasur.identidad.application.port.in.ActualizarUsuarioAdministrativoUseCase;
import co.edu.corhuila.rutasur.identidad.application.port.out.RolPuertoSalida;
import co.edu.corhuila.rutasur.identidad.application.port.out.UsuarioAdministracionPuertoSalida;
import co.edu.corhuila.rutasur.identidad.application.port.out.UsuarioPuertoSalida;
import co.edu.corhuila.rutasur.identidad.domain.exception.ExcepcionReglaNegocio;
import co.edu.corhuila.rutasur.identidad.domain.model.UsuarioAdministrable;

import java.util.Locale;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Actualiza una identidad y sustituye sus roles dentro de una transacción local.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ActualizarUsuarioAdministrativoServicio implements ActualizarUsuarioAdministrativoUseCase {
    private final UsuarioAdministracionPuertoSalida administracion;
    private final UsuarioPuertoSalida usuarios;
    private final RolPuertoSalida roles;

    /**
     * Valida cambios, actualiza datos y reemplaza roles activos seleccionados.
     */
    @Override
    @Transactional
    public UsuarioAdministrable actualizar(ActualizarUsuarioAdministrativoCommand comando) {
        UsuarioAdministrable actual = administracion.buscarPorId(comando.usuarioId()).orElseThrow(() -> new ExcepcionReglaNegocio("usuario.no_encontrado"));
        String correo = comando.correo().trim().toLowerCase(Locale.ROOT);
        String telefono = comando.telefono().trim();
        if (!actual.correo().equalsIgnoreCase(correo) && usuarios.existeCorreo(correo))
            throw new ExcepcionReglaNegocio("usuario.correo.duplicado");
        if (!actual.telefono().equals(telefono) && usuarios.existeTelefono(telefono))
            throw new ExcepcionReglaNegocio("usuario.telefono.duplicado");
        if (!roles.todosActivos(comando.roles())) throw new ExcepcionReglaNegocio("usuario.roles.invalidos");
        administracion.actualizar(actual.id(), comando.nombres().trim(), comando.apellidos().trim(), correo, telefono);
        administracion.reemplazarRoles(actual.id(), comando.roles());
        UUID correlacionId = comando.correlacionId() == null ? UUID.randomUUID() : comando.correlacionId();
        log.info("correlationId={} servicio=identidad casoUso=actualizarUsuarioAdministrativo evento=exito usuarioId={}", correlacionId, actual.id());
        return administracion.buscarPorId(actual.id()).orElseThrow(() -> new ExcepcionReglaNegocio("usuario.no_encontrado"));
    }
}
