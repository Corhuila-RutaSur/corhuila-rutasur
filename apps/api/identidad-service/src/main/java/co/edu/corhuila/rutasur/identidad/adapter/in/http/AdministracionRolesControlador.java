package co.edu.corhuila.rutasur.identidad.adapter.in.http;

import co.edu.corhuila.rutasur.identidad.application.port.in.ListarRolesUseCase;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

/**
 * Adaptador REST de roles, restringido al administrador autenticado.
 */
@RestController
@RequiredArgsConstructor
public class AdministracionRolesControlador implements AdministracionRolesApi {
    private final ListarRolesUseCase listarRoles;
    private final MessageSource mensajes;

    /**
     * Devuelve el catálogo de roles activos de la base de datos.
     */
    @Override
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<RespuestaGeneral<List<RolResponse>>> listarRoles() {
        List<RolResponse> respuesta = listarRoles.listar().stream().map(rol -> new RolResponse(rol.name())).toList();
        String mensaje = mensajes.getMessage("rol.listado.exitoso", null, LocaleContextHolder.getLocale());
        return ResponseEntity.ok(RespuestaGeneral.exitosa("200", "rol.listado.exitoso", mensaje, respuesta));
    }
}
