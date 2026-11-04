package co.edu.corhuila.rutasur.identidad.adapter.in.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import co.edu.corhuila.rutasur.identidad.adapter.in.http.RespuestaGeneral;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Locale;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

/**
 * Escribe errores de seguridad con el mismo contrato de respuesta de la API.
 */
@Component
@RequiredArgsConstructor
public class EscritorRespuestaSeguridad {
    private final ObjectMapper objectMapper;
    private final MessageSource mensajes;

    /**
     * Serializa una respuesta 401 localizada sin exponer detalles internos del token.
     */
    public void noAutorizado(HttpServletResponse respuesta, String codigo) throws IOException {
        Locale idioma = LocaleContextHolder.getLocale();
        String mensaje = mensajes.getMessage(codigo, null, mensajes.getMessage("error.interno", null, idioma), idioma);
        respuesta.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        respuesta.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(respuesta.getOutputStream(), RespuestaGeneral.error("401", codigo, mensaje));
    }
}
