package co.edu.corhuila.rutasur.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Actualiza las observaciones de una reserva pendiente sin alterar su trazabilidad. */
public record ReservaActualizacionRequest(@NotBlank @Size(max = 1000) String observaciones) { }
