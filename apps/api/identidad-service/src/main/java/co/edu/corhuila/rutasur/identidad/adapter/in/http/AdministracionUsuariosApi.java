package co.edu.corhuila.rutasur.identidad.adapter.in.http;

import jakarta.validation.Valid;

import java.util.UUID;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Contrato administrativo para crear y gestionar identidades.
 */
@RequestMapping("/api/v2/administracion/usuarios")
public interface AdministracionUsuariosApi {
    @GetMapping
    ResponseEntity<RespuestaGeneral<List<UsuarioAdministrativoResponse>>> listar();

    @GetMapping("/{usuarioId}")
    ResponseEntity<RespuestaGeneral<UsuarioAdministrativoResponse>> consultar(@PathVariable UUID usuarioId);

    @PatchMapping("/{usuarioId}")
    ResponseEntity<RespuestaGeneral<UsuarioAdministrativoResponse>> actualizar(@PathVariable UUID usuarioId,
                                                                               @Valid @RequestBody UsuarioAdministrativoActualizacionRequest solicitud,
                                                                               @RequestHeader(value = "X-Correlation-Id", required = false) UUID correlacionId);

    @DeleteMapping("/{usuarioId}")
    ResponseEntity<RespuestaGeneral<Void>> inactivar(@PathVariable UUID usuarioId,
                                                     @RequestHeader(value = "X-Correlation-Id", required = false) UUID correlacionId);

    @PostMapping
    ResponseEntity<RespuestaGeneral<UsuarioAdministrativoResponse>> crear(
            @Valid @RequestBody UsuarioAdministrativoRequest solicitud,
            @RequestHeader(value = "X-Correlation-Id", required = false) UUID correlacionId);
}
