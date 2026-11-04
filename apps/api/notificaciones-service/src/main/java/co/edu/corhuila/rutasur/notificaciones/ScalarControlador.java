package co.edu.corhuila.rutasur.notificaciones;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** Redirige a la interfaz Scalar servida como recurso estático. */
@Controller
public class ScalarControlador {

    @GetMapping({"/scalar", "/scalar/"})
    public String redirigirAScalar() {
        return "redirect:/scalar/index.html";
    }
}
