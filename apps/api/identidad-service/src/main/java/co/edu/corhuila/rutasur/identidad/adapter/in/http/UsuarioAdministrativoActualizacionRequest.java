package co.edu.corhuila.rutasur.identidad.adapter.in.http;

import co.edu.corhuila.rutasur.identidad.domain.model.RolUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Campos administrables de una identidad existente y sus nuevos roles.
 */
public record UsuarioAdministrativoActualizacionRequest(
        @NotBlank(message = "usuario.nombres.obligatorio") @Size(max = 100, message = "usuario.nombres.longitud") String nombres,
        @NotBlank(message = "usuario.apellidos.obligatorio") @Size(max = 100, message = "usuario.apellidos.longitud") String apellidos,
        @NotBlank(message = "usuario.correo.obligatorio") @Email(message = "usuario.correo.formato") String correo,
        @NotBlank(message = "usuario.telefono.obligatorio") @Pattern(regexp = "^3[0-9]{9}$", message = "usuario.telefono.formato") String telefono,
        @NotEmpty(message = "usuario.roles.obligatorios") List<RolUsuario> roles) {
}
