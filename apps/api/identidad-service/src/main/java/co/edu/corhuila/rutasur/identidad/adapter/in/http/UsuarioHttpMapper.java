package co.edu.corhuila.rutasur.identidad.adapter.in.http;

import co.edu.corhuila.rutasur.identidad.application.port.in.RegistrarClienteCommand;
import co.edu.corhuila.rutasur.identidad.domain.model.Usuario;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapea contratos HTTP sin filtrar detalles internos del agregado.
 */
@Mapper(componentModel = "spring")
public interface UsuarioHttpMapper {

    /**
     * Traduce la petición validada al comando de aplicación.
     */
    @Mapping(target = "correlacionId", source = "correlacionId")
    RegistrarClienteCommand aComando(UsuarioRegistroRequest solicitud, UUID correlacionId);

    /**
     * Expone únicamente los campos autorizados tras un registro.
     */
    @Mapping(target = "rol", constant = "CLIENTE")
    UsuarioRegistroResponse aRespuesta(Usuario usuario);
}
