package co.edu.corhuila.rutasur.dto.request;

import co.edu.corhuila.rutasur.validation.TelefonoColombiano;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Datos obligatorios para registrar una cuenta de usuario.
 *
 * @param tipoDocumento tipo del documento de identidad.
 * @param numeroDocumento numero unico del documento de identidad.
 * @param nombres nombres de la persona.
 * @param apellidos apellidos de la persona.
 * @param correo correo electronico unico de contacto e inicio de sesion.
 * @param telefono telefono movil colombiano unico.
 * @param contrasenia contrasenia en texto plano que sera codificada antes de persistirse.
 */
public record UsuarioRegistroRequest(
        @NotBlank @Size(max = 20) String tipoDocumento,
        @NotBlank @Size(max = 30) String numeroDocumento,
        @NotBlank @Size(max = 100) String nombres,
        @NotBlank @Size(max = 100) String apellidos,
        @NotBlank @Email @Size(max = 150) String correo,
        @NotBlank @TelefonoColombiano String telefono,
        @NotBlank @Size(min = 8, max = 72) String contrasenia) {
}
