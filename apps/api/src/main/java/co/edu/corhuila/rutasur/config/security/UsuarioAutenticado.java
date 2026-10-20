package co.edu.corhuila.rutasur.config.security;

import java.util.UUID;

/**
 * Principal autenticado expuesto a Spring Security.
 */
public record UsuarioAutenticado(Long usuarioId, UUID sesionId) {
}
