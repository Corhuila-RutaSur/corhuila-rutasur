package co.edu.corhuila.rutasur.config.security;

import co.edu.corhuila.rutasur.repository.AutenticacionRepositorio;
import co.edu.corhuila.rutasur.repository.UsuarioRepositorio;
import co.edu.corhuila.rutasur.service.JwtServicio;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Valida cada Bearer JWT antes de permitir acceder a recursos protegidos.
 */
@Component
public class FiltroJwt extends OncePerRequestFilter {
    private final JwtServicio jwt;
    private final AutenticacionRepositorio autenticacion;
    private final UsuarioRepositorio usuarios;
    private final EscritorRespuestaSeguridad escritorRespuesta;

    public FiltroJwt(JwtServicio jwt, AutenticacionRepositorio autenticacion, UsuarioRepositorio usuarios, EscritorRespuestaSeguridad escritorRespuesta) {
        this.jwt = jwt;
        this.autenticacion = autenticacion;
        this.usuarios = usuarios;
        this.escritorRespuesta = escritorRespuesta;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest solicitud, HttpServletResponse respuesta, FilterChain cadena) throws ServletException, IOException {
        String encabezado = solicitud.getHeader("Authorization");
        if (encabezado == null || !encabezado.startsWith("Bearer ")) {
            cadena.doFilter(solicitud, respuesta);
            return;
        }
        try {
            Claims claims = jwt.validar(encabezado.substring(7));
            Long usuarioId = Long.valueOf(claims.getSubject());
            UUID sesionId = UUID.fromString(claims.get("sid", String.class));
            UUID jti = UUID.fromString(claims.getId());
            if (usuarios.findByIdAndActivoTrue(usuarioId).isEmpty() || !autenticacion.sesionActiva(sesionId, usuarioId) || autenticacion.jtiRevocado(jti))
                throw new IllegalArgumentException();
            List<String> roles = claims.get("roles", List.class);
            var autoridades = roles.stream().map(rol -> new SimpleGrantedAuthority("ROLE_" + rol)).toList();
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(new UsuarioAutenticado(usuarioId, sesionId), null, autoridades));
            cadena.doFilter(solicitud, respuesta);
        } catch (ExpiredJwtException excepcion) {
            SecurityContextHolder.clearContext();
            escritorRespuesta.escribir(respuesta, HttpServletResponse.SC_UNAUTHORIZED, "autenticacion.token.expirado");
        } catch (JwtException | IllegalArgumentException excepcion) {
            SecurityContextHolder.clearContext();
            escritorRespuesta.escribir(respuesta, HttpServletResponse.SC_UNAUTHORIZED, "autenticacion.token.invalido");
        }
    }
}
