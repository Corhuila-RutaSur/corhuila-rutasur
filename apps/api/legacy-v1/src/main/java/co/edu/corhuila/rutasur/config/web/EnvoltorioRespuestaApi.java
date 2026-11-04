package co.edu.corhuila.rutasur.config.web;

import co.edu.corhuila.rutasur.dto.response.RespuestaGeneral;
import co.edu.corhuila.rutasur.exception.ResolvedorMensajes;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * Envuelve las respuestas exitosas de los controladores en el contrato publico de RutaSur.
 */
@RestControllerAdvice(basePackages = "co.edu.corhuila.rutasur.controller")
public class EnvoltorioRespuestaApi implements ResponseBodyAdvice<Object> {

    private final ResolvedorMensajes resolvedorMensajes;

    public EnvoltorioRespuestaApi(ResolvedorMensajes resolvedorMensajes) {
        this.resolvedorMensajes = resolvedorMensajes;
    }

    @Override
    public boolean supports(MethodParameter tipoRetorno, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object cuerpo, MethodParameter tipoRetorno, MediaType tipoContenido,
                                  Class converterType, ServerHttpRequest solicitud, ServerHttpResponse respuesta) {
        if (cuerpo instanceof RespuestaGeneral<?>) {
            return cuerpo;
        }
        int estado = respuesta instanceof ServletServerHttpResponse respuestaServlet
                ? respuestaServlet.getServletResponse().getStatus() : 200;
        return new RespuestaGeneral<>(String.valueOf(estado), "operacion.exitosa",
                resolvedorMensajes.resolver("operacion.exitosa"), cuerpo);
    }
}
