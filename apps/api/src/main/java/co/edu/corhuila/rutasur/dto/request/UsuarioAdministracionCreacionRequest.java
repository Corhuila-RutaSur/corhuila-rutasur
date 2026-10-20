package co.edu.corhuila.rutasur.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/** Datos para crear una cuenta desde administración y definir sus perfiles. */
public record UsuarioAdministracionCreacionRequest(
        @NotNull @Valid UsuarioRegistroRequest usuario,
        @NotEmpty List<String> roles) { }
