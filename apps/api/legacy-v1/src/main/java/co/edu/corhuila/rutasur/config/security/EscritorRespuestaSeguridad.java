package co.edu.corhuila.rutasur.config.security;

import co.edu.corhuila.rutasur.dto.response.RespuestaGeneral;
import co.edu.corhuila.rutasur.exception.ResolvedorMensajes;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

/**
 * Escribe respuestas de seguridad con el contrato publico uniforme de la API.
 */
@Component
@RequiredArgsConstructor
public class EscritorRespuestaSeguridad {

    private final ObjectMapper objectMapper;
    private final ResolvedorMensajes resolvedorMensajes;


    /**
     * Escribe una respuesta de autenticacion o autorizacion sin delegarla al contenedor.
     */
    public void escribir(HttpServletResponse respuesta, int estado, String codigo) throws IOException {
        respuesta.setStatus(estado);
        respuesta.setContentType(MediaType.APPLICATION_JSON_VALUE);
        respuesta.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(respuesta.getOutputStream(), new RespuestaGeneral<Void>(
                String.valueOf(estado), codigo, resolvedorMensajes.resolver(codigo), null));
    }
}
