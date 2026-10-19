package co.edu.corhuila.rutasur.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Datos de una persona que asistira al paquete seleccionado en la reserva. */
public record ParticipanteReservaRequest(
        @NotBlank @Size(max = 20) String tipoDocumento,
        @NotBlank @Size(max = 30) String numeroDocumento,
        @NotBlank @Size(max = 100) String nombres,
        @NotBlank @Size(max = 100) String apellidos,
        @NotBlank @Email @Size(max = 150) String correo,
        @NotBlank @Size(max = 30) String telefono,
        boolean responsable) { }
