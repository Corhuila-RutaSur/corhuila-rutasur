package co.edu.corhuila.rutasur.catalogo.adapter.in.http;

import jakarta.validation.constraints.Min;

/** Solicitud parcial para cambiar portada u orden de una imagen. */
public record ImagenExperienciaActualizacionRequest(Boolean principal, @Min(value = 0, message = "imagen.orden.invalido") Integer orden) {
}
