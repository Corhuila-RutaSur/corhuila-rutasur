package co.edu.corhuila.rutasur.identidad.application.usecase;

import co.edu.corhuila.rutasur.identidad.application.port.in.InactivarUsuarioUseCase;
import co.edu.corhuila.rutasur.identidad.application.port.out.AutenticacionPuertoSalida;
import co.edu.corhuila.rutasur.identidad.application.port.out.UsuarioAdministracionPuertoSalida;
import co.edu.corhuila.rutasur.identidad.domain.exception.ExcepcionReglaNegocio;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Inactiva una identidad y revoca sus sesiones sin borrar evidencia.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class InactivarUsuarioServicio implements InactivarUsuarioUseCase {
    private final UsuarioAdministracionPuertoSalida usuarios;
    private final AutenticacionPuertoSalida autenticacion;

    /**
     * Ejecuta eliminación lógica y revocación de sesiones en una transacción ACID.
     */
    @Override
    @Transactional
    public void inactivar(UUID usuarioId, UUID correlacionId) {
        var usuario = usuarios.buscarPorId(usuarioId).orElseThrow(() -> new ExcepcionReglaNegocio("usuario.no_encontrado"));
        if (!usuario.activo()) throw new ExcepcionReglaNegocio("usuario.inactivo");
        usuarios.inactivar(usuarioId);
        autenticacion.invalidarSesionesUsuario(usuarioId);
        UUID correlacionEfectiva = correlacionId == null ? UUID.randomUUID() : correlacionId;
        log.info("correlationId={} servicio=identidad casoUso=inactivarUsuario evento=exito usuarioId={}", correlacionEfectiva, usuarioId);
    }
}
