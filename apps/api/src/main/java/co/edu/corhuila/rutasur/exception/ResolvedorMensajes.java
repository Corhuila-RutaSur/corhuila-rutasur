package co.edu.corhuila.rutasur.exception;

import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

/**
 * Resuelve los mensajes expuestos por la API segun el idioma de la solicitud.
 */
@Component
public class ResolvedorMensajes {

    private final MessageSource fuenteMensajes;

    /**
     * Construye el resolvedor con la fuente central de mensajes.
     *
     * @param fuenteMensajes fuente configurada por Spring.
     */
    public ResolvedorMensajes(MessageSource fuenteMensajes) {
        this.fuenteMensajes = fuenteMensajes;
    }

    /**
     * Busca el literal internacionalizado para la solicitud actual.
     *
     * @param codigo clave de mensaje.
     * @return mensaje localizado o la clave si no existe una traduccion.
     */
    public String resolver(String codigo) {
        Locale locale = LocaleContextHolder.getLocale();
        return fuenteMensajes.getMessage(codigo, null, codigo, locale);
    }
}
