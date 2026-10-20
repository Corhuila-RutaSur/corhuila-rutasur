package co.edu.corhuila.rutasur.dto.request;

import co.edu.corhuila.rutasur.validation.TelefonoColombiano;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Datos obligatorios que un usuario puede actualizar en su perfil.
 *
 * @param tipoDocumento   tipo del documento de identidad.
 * @param numeroDocumento numero unico del documento de identidad.
 * @param nombres         nombres de la persona.
 * @param apellidos       apellidos de la persona.
 * @param correo          correo electronico unico de contacto.
 * @param telefono        telefono movil colombiano unico.
 */
public record UsuarioActualizacionRequest(
        @NotBlank @Size(max = 20) String tipoDocumento,
        @NotBlank @Size(max = 30) String numeroDocumento,
        @NotBlank @Size(max = 100) String nombres,
        @NotBlank @Size(max = 100) String apellidos,
        @NotBlank @Email @Size(max = 150) String correo,
        @NotBlank @TelefonoColombiano String telefono) {
}
