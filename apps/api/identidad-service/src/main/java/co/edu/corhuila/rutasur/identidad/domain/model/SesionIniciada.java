package co.edu.corhuila.rutasur.identidad.domain.model;

import java.util.List;
import java.util.UUID;

/**
 * Resultado de login que reúne datos seguros del usuario y tokens recién emitidos.
 */
public record SesionIniciada(UUID usuarioId, String correo, List<RolUsuario> roles, TokensAutenticacion tokens) {
}
