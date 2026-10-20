package co.edu.corhuila.rutasur.controller;

import co.edu.corhuila.rutasur.api.UsuarioApi;
import co.edu.corhuila.rutasur.dto.request.UsuarioActualizacionRequest;
import co.edu.corhuila.rutasur.dto.request.UsuarioRegistroRequest;
import co.edu.corhuila.rutasur.dto.request.UsuarioRolesActualizacionRequest;
import co.edu.corhuila.rutasur.dto.request.UsuarioAdministracionCreacionRequest;
import co.edu.corhuila.rutasur.dto.response.UsuarioResponse;
import co.edu.corhuila.rutasur.service.UsuarioServicio;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

/**
 * Adaptador REST que delega los casos de uso de usuarios al servicio de dominio.
 */
@RestController
public class UsuarioController implements UsuarioApi {

    private final UsuarioServicio usuarioServicio;

    /**
     * Construye el controlador de usuarios.
     *
     * @param usuarioServicio caso de uso que administra usuarios.
     */
    public UsuarioController(UsuarioServicio usuarioServicio) {
        this.usuarioServicio = usuarioServicio;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<UsuarioResponse> registrar(UsuarioRegistroRequest solicitud) {
        UsuarioResponse usuario = usuarioServicio.registrar(solicitud);
        return ResponseEntity.created(URI.create("/api/v1/usuarios/" + usuario.id())).body(usuario);
    }

    @Override
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<UsuarioResponse> registrarAdministrativo(UsuarioAdministracionCreacionRequest solicitud) {
        UsuarioResponse usuario = usuarioServicio.registrarAdministrativo(solicitud.usuario(), solicitud.roles());
        return ResponseEntity.created(URI.create("/api/v1/usuarios/" + usuario.id())).body(usuario);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasRole('ADMINISTRADOR') or #id == authentication.principal.usuarioId")
    public ResponseEntity<UsuarioResponse> consultarPorId(Long id) {
        return ResponseEntity.ok(usuarioServicio.consultarPorId(id));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<UsuarioResponse>> consultarActivos() {
        return ResponseEntity.ok(usuarioServicio.consultarActivos());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasRole('ADMINISTRADOR') or #id == authentication.principal.usuarioId")
    public ResponseEntity<UsuarioResponse> actualizar(Long id, UsuarioActualizacionRequest solicitud) {
        return ResponseEntity.ok(usuarioServicio.actualizar(id, solicitud));
    }

    @Override
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<UsuarioResponse> actualizarRoles(Long id, UsuarioRolesActualizacionRequest solicitud) {
        return ResponseEntity.ok(usuarioServicio.actualizarRoles(id, solicitud.roles()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasRole('ADMINISTRADOR') or #id == authentication.principal.usuarioId")
    public ResponseEntity<Void> eliminarLogicamente(Long id) {
        usuarioServicio.eliminarLogicamente(id);
        return ResponseEntity.noContent().build();
    }
}
