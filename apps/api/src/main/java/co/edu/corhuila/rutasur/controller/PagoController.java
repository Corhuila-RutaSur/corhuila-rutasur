package co.edu.corhuila.rutasur.controller;

import co.edu.corhuila.rutasur.dto.request.PagoSimuladoRequest;
import co.edu.corhuila.rutasur.dto.response.PagoResponse;
import co.edu.corhuila.rutasur.service.PagoServicio;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Expone el pago simulado protegido para el titular de una reserva.
 */
@RestController
@RequiredArgsConstructor
public class PagoController {
    private final PagoServicio pagoServicio;

    @PostMapping("/api/v1/reservas/{reservaId}/pagos/simulado")
    @PreAuthorize("hasRole('ADMINISTRADOR') or @autorizacionReserva.esPropietario(#reservaId, authentication.principal.usuarioId)")
    public ResponseEntity<PagoResponse> pagarSimulado(@PathVariable Long reservaId, @Valid @RequestBody PagoSimuladoRequest solicitud) {
        return ResponseEntity.ok(pagoServicio.pagarSimulado(reservaId, solicitud));
    }
}
