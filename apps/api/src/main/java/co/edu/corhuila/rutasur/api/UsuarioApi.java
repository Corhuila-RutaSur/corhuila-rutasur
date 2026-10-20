package co.edu.corhuila.rutasur.api;

import co.edu.corhuila.rutasur.dto.request.UsuarioActualizacionRequest;
import co.edu.corhuila.rutasur.dto.request.UsuarioRegistroRequest;
import co.edu.corhuila.rutasur.dto.request.UsuarioRolesActualizacionRequest;
import co.edu.corhuila.rutasur.dto.request.UsuarioAdministracionCreacionRequest;
import co.edu.corhuila.rutasur.dto.response.UsuarioResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Define el contrato HTTP y la documentacion OpenAPI de usuarios.
 */
@Tag(name = "Usuarios", description = "Registro y administracion logica de cuentas de usuario")
public interface UsuarioApi {

    @Operation(summary = "Crear usuario administrativo", description = "Crea una cuenta y asigna uno o varios roles; requiere ADMINISTRADOR.")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/api/v1/usuarios")
    ResponseEntity<UsuarioResponse> registrarAdministrativo(@Valid @RequestBody UsuarioAdministracionCreacionRequest solicitud);

    /**
     * Registra una nueva cuenta desde un canal publico.
     *
     * @param solicitud datos obligatorios de registro.
     * @return cuenta creada sin contrasenia ni hash.
     */
    @Operation(summary = "Registrar usuario", description = "Crea una cuenta activa y codifica la contrasenia con BCrypt.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario creado"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos", content = @Content),
            @ApiResponse(responseCode = "409", description = "Documento, correo o telefono duplicado", content = @Content)
    })
    @PostMapping("/api/v1/public/usuarios")
    ResponseEntity<UsuarioResponse> registrar(@Valid @RequestBody UsuarioRegistroRequest solicitud);

    /**
     * Consulta un usuario activo por su identificador.
     *
     * @param id identificador tecnico.
     * @return datos visibles del usuario.
     */
    @Operation(summary = "Consultar usuario", description = "Obtiene una cuenta activa sin exponer credenciales.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    })
    @GetMapping("/api/v1/usuarios/{id}")
    ResponseEntity<UsuarioResponse> consultarPorId(@PathVariable Long id);

    /**
     * Lista las cuentas de usuario activas.
     *
     * @return listado ligero de usuarios activos.
     */
    @Operation(summary = "Listar usuarios activos", description = "Usa una proyeccion de lectura y no expone credenciales.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Listado consultado")
    @GetMapping("/api/v1/usuarios")
    ResponseEntity<List<UsuarioResponse>> consultarActivos();

    /**
     * Actualiza los datos de perfil de un usuario activo.
     *
     * @param id identificador tecnico.
     * @param solicitud datos completos de actualizacion.
     * @return usuario actualizado.
     */
    @Operation(summary = "Actualizar usuario", description = "Actualiza datos personales; el cambio de contrasenia se tratara en un flujo dedicado.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario actualizado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Documento, correo o telefono duplicado", content = @Content)
    })
    @PutMapping("/api/v1/usuarios/{id}")
    ResponseEntity<UsuarioResponse> actualizar(
            @PathVariable Long id, @Valid @RequestBody UsuarioActualizacionRequest solicitud);

    @Operation(summary = "Asignar roles", description = "Reemplaza los roles de una cuenta; requiere ADMINISTRADOR.")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/api/v1/usuarios/{id}/roles")
    ResponseEntity<UsuarioResponse> actualizarRoles(
            @PathVariable Long id, @Valid @RequestBody UsuarioRolesActualizacionRequest solicitud);

    /**
     * Inactiva una cuenta y conserva su informacion historica.
     *
     * @param id identificador tecnico.
     * @return respuesta HTTP sin contenido.
     */
    @Operation(summary = "Inactivar usuario", description = "Ejecuta eliminacion logica y no borra registros fisicos.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuario inactivado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    })
    @DeleteMapping("/api/v1/usuarios/{id}")
    ResponseEntity<Void> eliminarLogicamente(@PathVariable Long id);
}
