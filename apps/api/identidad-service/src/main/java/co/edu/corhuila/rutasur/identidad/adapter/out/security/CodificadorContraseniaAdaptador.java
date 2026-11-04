package co.edu.corhuila.rutasur.identidad.adapter.out.security;

import co.edu.corhuila.rutasur.identidad.application.port.out.CodificadorContraseniaPuerto;
import co.edu.corhuila.rutasur.identidad.application.port.out.VerificadorContraseniaPuerto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Adaptador BCrypt usado exclusivamente para codificar contraseñas.
 */
@Component
@RequiredArgsConstructor
public class CodificadorContraseniaAdaptador implements CodificadorContraseniaPuerto, VerificadorContraseniaPuerto {
    private final PasswordEncoder passwordEncoder;

    @Override
    public String codificar(String contrasenia) {
        return passwordEncoder.encode(contrasenia);
    }

    @Override
    public boolean coincide(String contrasenia, String hash) {
        return passwordEncoder.matches(contrasenia, hash);
    }
}
