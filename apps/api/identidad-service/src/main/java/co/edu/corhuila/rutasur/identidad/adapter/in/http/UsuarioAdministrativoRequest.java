package co.edu.corhuila.rutasur.identidad.adapter.in.http;

import co.edu.corhuila.rutasur.identidad.domain.model.RolUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Datos requeridos para crear una identidad desde el panel administrativo.
 */
public record UsuarioAdministrativoRequest(
        @NotBlank(message = "usuario.tipo_documento.obligatorio") @Size(max = 20, message = "usuario.tipo_documento.longitud") String tipoDocumento,
        @NotBlank(message = "usuario.numero_documento.obligatorio") @Size(max = 30, message = "usuario.numero_documento.longitud") String numeroDocumento,
        @NotBlank(message = "usuario.nombres.obligatorio") @Size(max = 100, message = "usuario.nombres.longitud") String nombres,
        @NotBlank(message = "usuario.apellidos.obligatorio") @Size(max = 100, message = "usuario.apellidos.longitud") String apellidos,
        @NotBlank(message = "usuario.correo.obligatorio") @Email(message = "usuario.correo.formato") String correo,
        @NotBlank(message = "usuario.telefono.obligatorio") @Pattern(regexp = "^3[0-9]{9}$", message = "usuario.telefono.formato") String telefono,
        @NotBlank(message = "usuario.contrasenia.obligatoria") @Size(min = 12, max = 100, message = "usuario.contrasenia.longitud") String contrasenia,
        @NotEmpty(message = "usuario.roles.obligatorios") List<RolUsuario> roles) {
}
