package co.edu.corhuila.rutasur.identidad.adapter.in.security;

import co.edu.corhuila.rutasur.identidad.application.port.out.AutenticacionPuertoSalida;
import co.edu.corhuila.rutasur.identidad.application.port.out.TokenAutenticacionPuertoSalida;
import co.edu.corhuila.rutasur.identidad.domain.exception.ExcepcionAutenticacion;
import co.edu.corhuila.rutasur.identidad.domain.model.TokenAccesoValidado;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Intercepta rutas protegidas y valida JWT, sesión activa y revocación en Identidad.
 */
@Component
@RequiredArgsConstructor
public class FiltroJwt extends OncePerRequestFilter {
    private final TokenAutenticacionPuertoSalida tokens;
    private final AutenticacionPuertoSalida autenticacion;
    private final EscritorRespuestaSeguridad escritor;

    /**
     * Omite contratos públicos y salud operativa; todo lo demás requiere token.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest solicitud) {
        String ruta = solicitud.getServletPath();
        return ruta.startsWith("/api/v2/public/") || ruta.startsWith("/actuator/")
                || ruta.startsWith("/v3/api-docs") || ruta.startsWith("/swagger-ui")
                || ruta.equals("/swagger-ui.html") || ruta.equals("/scalar") || ruta.startsWith("/scalar/");
    }

    /**
     * Instala autoridades solamente cuando JWT, sesión y jti siguen vigentes.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest solicitud, HttpServletResponse respuesta, FilterChain cadena)
            throws ServletException, IOException {
        String autorizacion = solicitud.getHeader("Authorization");
        if (autorizacion == null || !autorizacion.startsWith("Bearer ")) {
            escritor.noAutorizado(respuesta, "autenticacion.token.invalido");
            return;
        }
        try {
            TokenAccesoValidado token = tokens.validarAccessToken(autorizacion.substring(7));
            if (!autenticacion.sesionActiva(token.sesionId()) || autenticacion.accessTokenRevocado(token.jti())) {
                throw new ExcepcionAutenticacion("autenticacion.token.invalido");
            }
            var autoridades = token.roles().stream().map(rol -> new SimpleGrantedAuthority("ROLE_" + rol.name())).toList();
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(token.usuarioId(), null, autoridades));
            cadena.doFilter(solicitud, respuesta);
        } catch (ExcepcionAutenticacion excepcion) {
            SecurityContextHolder.clearContext();
            escritor.noAutorizado(respuesta, excepcion.getMessage());
        }
    }
}
