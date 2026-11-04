package co.edu.corhuila.rutasur.catalogo.application.port.out;

import co.edu.corhuila.rutasur.catalogo.application.port.in.ArchivoImagenCommand;
import java.util.UUID;

/** Abstrae el almacenamiento físico de archivos de imagen. */
public interface AlmacenamientoImagenPuertoSalida {
    String guardar(UUID experienciaId, UUID imagenId, ArchivoImagenCommand archivo);
    void eliminar(String ruta);
}
