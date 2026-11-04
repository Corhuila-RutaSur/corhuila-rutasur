package co.edu.corhuila.rutasur.identidad.adapter.in.http;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Contrato administrativo para consultar los roles realmente habilitados.
 */
@RequestMapping("/api/v2/administracion/roles")
public interface AdministracionRolesApi {
    @GetMapping
    ResponseEntity<RespuestaGeneral<List<RolResponse>>> listarRoles();
}
