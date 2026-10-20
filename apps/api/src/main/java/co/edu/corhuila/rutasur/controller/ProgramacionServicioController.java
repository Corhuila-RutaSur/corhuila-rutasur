package co.edu.corhuila.rutasur.controller;

import co.edu.corhuila.rutasur.dto.request.ProgramacionServicioRequest;
import co.edu.corhuila.rutasur.dto.response.ProgramacionServicioResponse;
import co.edu.corhuila.rutasur.service.ProgramacionServicioServicio;
import jakarta.validation.Valid;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * API de precios y cupos de servicios por salida.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ProgramacionServicioController {
    private final ProgramacionServicioServicio servicio;

    @GetMapping("/public/salidas/{salidaId}/programaciones")
    public ResponseEntity<List<ProgramacionServicioResponse>> listarPublicas(@PathVariable Long salidaId) {
        return ResponseEntity.ok(servicio.listar(salidaId));
    }

    @GetMapping("/salidas/{salidaId}/programaciones")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<ProgramacionServicioResponse>> listar(@PathVariable Long salidaId) {
        return ResponseEntity.ok(servicio.listar(salidaId));
    }

    @PostMapping("/salidas/{salidaId}/programaciones")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ProgramacionServicioResponse> crear(@PathVariable Long salidaId, @Valid @RequestBody ProgramacionServicioRequest solicitud) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicio.crear(salidaId, solicitud));
    }

    @PutMapping("/salidas/{salidaId}/programaciones/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ProgramacionServicioResponse> actualizar(@PathVariable Long salidaId, @PathVariable Long id, @Valid @RequestBody ProgramacionServicioRequest solicitud) {
        return ResponseEntity.ok(servicio.actualizar(salidaId, id, solicitud));
    }

    @DeleteMapping("/salidas/{salidaId}/programaciones/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> inactivar(@PathVariable Long salidaId, @PathVariable Long id) {
        servicio.inactivar(salidaId, id);
        return ResponseEntity.noContent().build();
    }
}
