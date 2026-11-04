package co.edu.corhuila.rutasur.identidad.adapter.in.http;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Contrato protegido para operar la sesión autenticada actual.
 */
@RequestMapping("/api/v2/autenticacion")
public interface SesionApi {

    /**
     * Cierra la sesión representada por el JWT Bearer recibido.
     */
    @PostMapping("/logout")
    ResponseEntity<RespuestaGeneral<Void>> cerrarSesion(
            @RequestHeader("Authorization") String autorizacion,
            @RequestHeader(value = "X-Correlation-Id", required = false) UUID correlacionId);
}
