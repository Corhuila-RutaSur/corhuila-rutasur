package co.edu.corhuila.rutasur.identidad.adapter.in.http;

import co.edu.corhuila.rutasur.identidad.application.port.in.CrearUsuarioAdministrativoCommand;
import co.edu.corhuila.rutasur.identidad.application.port.in.CrearUsuarioAdministrativoUseCase;
import co.edu.corhuila.rutasur.identidad.application.port.in.ConsultarUsuariosAdministracionUseCase;
import co.edu.corhuila.rutasur.identidad.application.port.in.ActualizarUsuarioAdministrativoCommand;
import co.edu.corhuila.rutasur.identidad.application.port.in.ActualizarUsuarioAdministrativoUseCase;
import co.edu.corhuila.rutasur.identidad.application.port.in.InactivarUsuarioUseCase;
import co.edu.corhuila.rutasur.identidad.domain.model.UsuarioAdministrable;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

/**
 * Adaptador REST exclusivo para administración autorizada de usuarios.
 */
@RestController
@RequiredArgsConstructor
public class AdministracionUsuariosControlador implements AdministracionUsuariosApi {
    private final CrearUsuarioAdministrativoUseCase crearUsuario;
    private final ConsultarUsuariosAdministracionUseCase consultarUsuarios;
    private final ActualizarUsuarioAdministrativoUseCase actualizarUsuario;
    private final InactivarUsuarioUseCase inactivarUsuario;
    private final MessageSource mensajes;

    /**
     * Lista identidades mediante proyecciones administrativas seguras.
     */
    @Override
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<RespuestaGeneral<List<UsuarioAdministrativoResponse>>> listar() {
        String mensaje = mensajes.getMessage("usuario.administrativo.listado", null, LocaleContextHolder.getLocale());
        return ResponseEntity.ok(RespuestaGeneral.exitosa("200", "usuario.administrativo.listado", mensaje,
                consultarUsuarios.listar().stream().map(this::aRespuesta).toList()));
    }

    /**
     * Consulta una identidad determinada para administración autorizada.
     */
    @Override
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<RespuestaGeneral<UsuarioAdministrativoResponse>> consultar(UUID usuarioId) {
        String mensaje = mensajes.getMessage("usuario.administrativo.consultado", null, LocaleContextHolder.getLocale());
        return ResponseEntity.ok(RespuestaGeneral.exitosa("200", "usuario.administrativo.consultado", mensaje,
                aRespuesta(consultarUsuarios.consultar(usuarioId))));
    }

    private UsuarioAdministrativoResponse aRespuesta(UsuarioAdministrable usuario) {
        return new UsuarioAdministrativoResponse(usuario.id(), usuario.tipoDocumento(), usuario.numeroDocumento(), usuario.nombres(), usuario.apellidos(), usuario.correo(), usuario.telefono(), usuario.activo(), usuario.roles().stream().map(Enum::name).toList(), usuario.creadoEn());
    }

    /**
     * Actualiza datos editables y reemplaza roles mediante administración autorizada.
     */
    @Override
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<RespuestaGeneral<UsuarioAdministrativoResponse>> actualizar(UUID usuarioId,
                                                                                      UsuarioAdministrativoActualizacionRequest solicitud, UUID correlacionId) {
        UUID correlacionEfectiva = correlacionId == null ? UUID.randomUUID() : correlacionId;
        var comando = new ActualizarUsuarioAdministrativoCommand(usuarioId, solicitud.nombres(), solicitud.apellidos(), solicitud.correo(), solicitud.telefono(), solicitud.roles(), correlacionEfectiva);
        String mensaje = mensajes.getMessage("usuario.administrativo.actualizado", null, LocaleContextHolder.getLocale());
        return ResponseEntity.ok().header("X-Correlation-Id", correlacionEfectiva.toString())
                .body(RespuestaGeneral.exitosa("200", "usuario.administrativo.actualizado", mensaje, aRespuesta(actualizarUsuario.actualizar(comando))));
    }

    /**
     * Inactiva lógicamente una identidad y sus sesiones desde administración.
     */
    @Override
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<RespuestaGeneral<Void>> inactivar(UUID usuarioId, UUID correlacionId) {
        UUID correlacionEfectiva = correlacionId == null ? UUID.randomUUID() : correlacionId;
        inactivarUsuario.inactivar(usuarioId, correlacionEfectiva);
        String mensaje = mensajes.getMessage("usuario.administrativo.inactivado", null, LocaleContextHolder.getLocale());
        return ResponseEntity.ok().header("X-Correlation-Id", correlacionEfectiva.toString())
                .body(RespuestaGeneral.exitosa("200", "usuario.administrativo.inactivado", mensaje, null));
    }

    /**
     * Crea un usuario con roles reales elegidos por un administrador.
     */
    @Override
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<RespuestaGeneral<UsuarioAdministrativoResponse>> crear(UsuarioAdministrativoRequest solicitud, UUID correlacionId) {
        UUID correlacionEfectiva = correlacionId == null ? UUID.randomUUID() : correlacionId;
        CrearUsuarioAdministrativoCommand comando = new CrearUsuarioAdministrativoCommand(solicitud.tipoDocumento(), solicitud.numeroDocumento(),
                solicitud.nombres(), solicitud.apellidos(), solicitud.correo(), solicitud.telefono(), solicitud.contrasenia(), solicitud.roles(), correlacionEfectiva);
        String mensaje = mensajes.getMessage("usuario.administrativo.creado", null, LocaleContextHolder.getLocale());
        var creado = crearUsuario.crear(comando);
        var respuesta = new UsuarioAdministrativoResponse(creado.id(), creado.tipoDocumento(), creado.numeroDocumento(), creado.nombres(),
                creado.apellidos(), creado.correo(), creado.telefono(), creado.activo(), solicitud.roles().stream().map(Enum::name).distinct().toList(), creado.creadoEn());
        return ResponseEntity.status(HttpStatus.CREATED).header("X-Correlation-Id", correlacionEfectiva.toString())
                .body(RespuestaGeneral.exitosa("201", "usuario.administrativo.creado", mensaje, respuesta));
    }
}
