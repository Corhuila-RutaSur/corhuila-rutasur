package co.edu.corhuila.rutasur.identidad.adapter.in.http;

import jakarta.validation.Valid;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Contrato HTTP público para las identidades de clientes.
 */
@RequestMapping("/api/v2/public/usuarios")
public interface UsuarioPublicoApi {

    /**
     * Registra un cliente y asigna exclusivamente el rol CLIENTE.
     */
    @PostMapping
    ResponseEntity<RespuestaGeneral<UsuarioRegistroResponse>> registrar(
            @Valid @RequestBody UsuarioRegistroRequest solicitud,
            @RequestHeader(value = "X-Correlation-Id", required = false) UUID correlacionId);
}
