package co.edu.corhuila.rutasur.config.trazabilidad;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Registra una miga de pan segura por solicitud sin exponer cuerpos ni credenciales.
 */
@Component
@Slf4j
public class FiltroTrazabilidad extends OncePerRequestFilter {

    /**
     * Agrega un identificador correlacionable y registra inicio y cierre de cada solicitud.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest solicitud, HttpServletResponse respuesta, FilterChain cadena)
            throws ServletException, IOException {
        String idTraza = UUID.randomUUID().toString();
        long inicio = System.currentTimeMillis();
        MDC.put("traza", idTraza);
        try {
            log.info("traza={} evento=solicitud_inicio metodo={} ruta={}", idTraza,
                    solicitud.getMethod(), solicitud.getRequestURI());
            cadena.doFilter(solicitud, respuesta);
        } finally {
            log.info("traza={} evento=solicitud_fin estado={} duracionMs={}", idTraza,
                    respuesta.getStatus(), System.currentTimeMillis() - inicio);
            MDC.remove("traza");
        }
    }
}
