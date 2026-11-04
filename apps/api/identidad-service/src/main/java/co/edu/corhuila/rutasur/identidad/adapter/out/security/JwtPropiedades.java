package co.edu.corhuila.rutasur.identidad.adapter.out.security;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Propiedades externas requeridas para firmar tokens JWT de Identidad.
 */
@Validated
@ConfigurationProperties(prefix = "rutasur.seguridad.jwt")
public record JwtPropiedades(
        @NotBlank(message = "El secreto JWT es obligatorio.") @Size(min = 32, message = "El secreto JWT debe tener al menos 32 caracteres.") String secreto,
        @Min(value = 1, message = "La vigencia access debe ser positiva.") long accessMinutos,
        @Min(value = 1, message = "La vigencia refresh debe ser positiva.") long refreshDias) {
}
