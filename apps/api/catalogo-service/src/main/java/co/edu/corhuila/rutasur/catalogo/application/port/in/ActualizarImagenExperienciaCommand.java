package co.edu.corhuila.rutasur.catalogo.application.port.in;

import java.util.UUID;

/** Cambio administrativo de portada u orden de una imagen. */
public record ActualizarImagenExperienciaCommand(Boolean principal, Integer orden, UUID correlacionId) {
}
