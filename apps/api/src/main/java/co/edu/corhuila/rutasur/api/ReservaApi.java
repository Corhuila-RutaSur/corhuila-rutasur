package co.edu.corhuila.rutasur.api;

import co.edu.corhuila.rutasur.dto.request.ReservaActualizacionRequest;
import co.edu.corhuila.rutasur.dto.request.ReservaCreacionRequest;
import co.edu.corhuila.rutasur.dto.response.ReservaResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/** Contrato HTTP de creación y administración inicial de reservas. */
@Tag(name = "Reservas", description = "Reservas configurables de experiencias")
public interface ReservaApi {
    /** Crea una reserva pendiente y congela precios y cupos. */
    @Operation(summary = "Crear reserva", description = "Ruta pública temporal hasta implementar JWT.")
    @ApiResponse(responseCode = "201", description = "Reserva creada")
    @PostMapping("/api/v1/public/reservas")
    ResponseEntity<ReservaResponse> crear(@Valid @RequestBody ReservaCreacionRequest solicitud);

    /** Consulta una reserva vigente por identificador. */
    @Operation(summary = "Consultar reserva") @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/api/v1/reservas/{id}")
    ResponseEntity<ReservaResponse> consultarPorId(@PathVariable Long id);

    /** Lista las reservas históricas de un usuario. */
    @Operation(summary = "Listar reservas por usuario") @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/api/v1/usuarios/{usuarioId}/reservas")
    ResponseEntity<List<ReservaResponse>> consultarPorUsuario(@PathVariable Long usuarioId);

    /** Actualiza observaciones de una reserva pendiente. */
    @Operation(summary = "Actualizar observaciones de reserva") @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/api/v1/reservas/{id}")
    ResponseEntity<ReservaResponse> actualizar(@PathVariable Long id,
            @Valid @RequestBody ReservaActualizacionRequest solicitud);

    /** Cancela una reserva sin eliminar su historial. */
    @Operation(summary = "Cancelar reserva") @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/api/v1/reservas/{id}")
    ResponseEntity<Void> cancelar(@PathVariable Long id);
}
