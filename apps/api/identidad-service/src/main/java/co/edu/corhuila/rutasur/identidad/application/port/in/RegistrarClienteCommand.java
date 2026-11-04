package co.edu.corhuila.rutasur.identidad.application.port.in;

import java.util.UUID;

/**
 * Datos validados que ingresan al caso de uso de registro.
 */
public record RegistrarClienteCommand(String tipoDocumento, String numeroDocumento, String nombres,
                                      String apellidos, String correo, String telefono, String contrasenia,
                                      UUID correlacionId) {
}
