package co.edu.corhuila.rutasur.reservas.adapter.in.http;

import jakarta.validation.constraints.*;

import java.math.*;
import java.util.*;

/**
 * Solicitud para habilitar una actividad de Catálogo en una salida.
 */
public record ProgramacionActividadRequest(@NotNull UUID actividadIdExterna, @NotBlank String nombre,
                                           @NotNull @DecimalMin("0.0") BigDecimal precio) {
}
