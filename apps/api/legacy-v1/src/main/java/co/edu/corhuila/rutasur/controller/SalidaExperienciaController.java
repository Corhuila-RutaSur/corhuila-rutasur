package co.edu.corhuila.rutasur.controller;

import co.edu.corhuila.rutasur.dto.request.SalidaExperienciaRequest;
import co.edu.corhuila.rutasur.dto.response.SalidaExperienciaResponse;
import co.edu.corhuila.rutasur.service.SalidaExperienciaServicio;
import jakarta.validation.Valid;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Adaptador HTTP para administrar fechas y cupos generales de experiencias.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class SalidaExperienciaController {
    private final SalidaExperienciaServicio servicio;

    @GetMapping("/public/experiencias/{experienciaId}/salidas")
    public ResponseEntity<List<SalidaExperienciaResponse>> listarPublicas(@PathVariable Long experienciaId) {
        return ResponseEntity.ok(servicio.listarPublicas(experienciaId));
    }

    @GetMapping("/public/salidas/{id}")
    public ResponseEntity<SalidaExperienciaResponse> consultarPublica(@PathVariable Long id) {
        return ResponseEntity.ok(servicio.consultarPublica(id));
    }

    @GetMapping("/experiencias/{experienciaId}/salidas")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<SalidaExperienciaResponse>> listar(@PathVariable Long experienciaId) {
        return ResponseEntity.ok(servicio.listar(experienciaId));
    }

    @PostMapping("/experiencias/{experienciaId}/salidas")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<SalidaExperienciaResponse> crear(@PathVariable Long experienciaId, @Valid @RequestBody SalidaExperienciaRequest solicitud) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicio.crear(experienciaId, solicitud));
    }

    @PutMapping("/experiencias/{experienciaId}/salidas/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<SalidaExperienciaResponse> actualizar(@PathVariable Long experienciaId, @PathVariable Long id, @Valid @RequestBody SalidaExperienciaRequest solicitud) {
        return ResponseEntity.ok(servicio.actualizar(experienciaId, id, solicitud));
    }

    @DeleteMapping("/experiencias/{experienciaId}/salidas/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> cancelar(@PathVariable Long experienciaId, @PathVariable Long id) {
        servicio.inactivar(experienciaId, id);
        return ResponseEntity.noContent().build();
    }
}
