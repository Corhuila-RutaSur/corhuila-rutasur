package co.edu.corhuila.rutasur.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@EnableConfigurationProperties(JwtProperties.class)
@RequiredArgsConstructor
public class SecurityConfig {
    private final FiltroJwt filtroJwt;
    private final EscritorRespuestaSeguridad escritorRespuestaSeguridad;

    /**
     * Construye el codificador irreversible de contrasenias para las credenciales locales.
     *
     * @return codificador BCrypt con factor de trabajo 12.
     */
    @Bean
    PasswordEncoder codificadorContrasenias() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * Define una API sin sesion HTTP y separa el registro publico de las operaciones protegidas.
     *
     * @param http configurador de seguridad de Spring.
     * @return cadena de filtros configurada.
     * @throws Exception si Spring Security no puede construir la cadena.
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(filtroJwt, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(
                                "/error",
                                "/actuator/health",
                                "/actuator/info",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/scalar/**",
                                "/uploads/**",
                                "/api/v1/public/**")
                        .permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(
                        (request, response, authenticationException) -> escritorRespuestaSeguridad.escribir(
                                response, 401, "autenticacion.requerida")))
                .build();
    }
}
