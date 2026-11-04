package co.edu.corhuila.rutasur.identidad.adapter.in.http;

import co.edu.corhuila.rutasur.identidad.application.port.in.IniciarSesionCommand;
import co.edu.corhuila.rutasur.identidad.application.port.in.RenovarSesionCommand;
import co.edu.corhuila.rutasur.identidad.domain.model.SesionIniciada;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Traduce contratos HTTP del módulo de autenticación a modelos de aplicación.
 */
@Mapper(componentModel = "spring")
public interface AutenticacionHttpMapper {

    /**
     * Construye el comando de login con contexto técnico y correlación.
     */
    @Mapping(target = "ipOrigen", source = "ipOrigen")
    @Mapping(target = "agenteUsuario", source = "agenteUsuario")
    @Mapping(target = "correlacionId", source = "correlacionId")
    IniciarSesionCommand aComando(AutenticacionLoginRequest solicitud, String ipOrigen, String agenteUsuario,
                                  UUID correlacionId);

    /**
     * Construye el comando de refresh incluyendo la correlación de la solicitud.
     */
    @Mapping(target = "correlacionId", source = "correlacionId")
    RenovarSesionCommand aComando(AutenticacionRefreshRequest solicitud, UUID correlacionId);

    /**
     * Expone únicamente datos necesarios para el cliente autenticado.
     */
    @Mapping(target = "roles", expression = "java(resultado.roles().stream().map(Enum::name).toList())")
    @Mapping(target = "accessToken", source = "tokens.accessToken")
    @Mapping(target = "refreshToken", source = "tokens.refreshToken")
    @Mapping(target = "accessExpiraEn", source = "tokens.accessExpiraEn")
    @Mapping(target = "refreshExpiraEn", source = "tokens.refreshExpiraEn")
    AutenticacionLoginResponse aRespuesta(SesionIniciada resultado);
}
