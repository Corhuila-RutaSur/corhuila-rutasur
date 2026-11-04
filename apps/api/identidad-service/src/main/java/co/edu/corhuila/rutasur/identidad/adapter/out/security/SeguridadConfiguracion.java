package co.edu.corhuila.rutasur.identidad.adapter.out.security;

import co.edu.corhuila.rutasur.identidad.adapter.in.security.EscritorRespuestaSeguridad;
import co.edu.corhuila.rutasur.identidad.adapter.in.security.FiltroJwt;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Configura BCrypt con el factor de trabajo definido por el dominio.
 */
@Configuration
@EnableMethodSecurity
public class SeguridadConfiguracion {
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /** Permite que el Angular local consuma la API durante la transición sin Gateway. */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuracion = new CorsConfiguration();
        configuracion.setAllowedOrigins(List.of("http://localhost:4200", "http://localhost:4201"));
        configuracion.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuracion.setAllowedHeaders(List.of("*"));
        configuracion.setExposedHeaders(List.of("Authorization", "X-Correlation-Id"));
        configuracion.setAllowCredentials(true);
        configuracion.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource origenes = new UrlBasedCorsConfigurationSource();
        origenes.registerCorsConfiguration("/**", configuracion);
        return origenes;
    }

    /**
     * Define API sin sesión HTTP: solo rutas públicas y salud quedan fuera del filtro JWT.
     */
    @Bean
    SecurityFilterChain cadenaSeguridad(HttpSecurity seguridad, FiltroJwt filtroJwt, EscritorRespuestaSeguridad escritor) throws Exception {
        return seguridad.csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(configuracion -> configuracion.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(reglas -> reglas.requestMatchers("/api/v2/public/**", "/actuator/**",
                        "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/scalar", "/scalar/**").permitAll().anyRequest().authenticated())
                .exceptionHandling(configuracion -> configuracion.authenticationEntryPoint((solicitud, respuesta, error) ->
                        escritor.noAutorizado(respuesta, "autenticacion.token.invalido")))
                .addFilterBefore(filtroJwt, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
