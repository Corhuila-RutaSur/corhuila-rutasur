package co.edu.corhuila.rutasur.catalogo.adapter.out.storage;

import co.edu.corhuila.rutasur.catalogo.application.port.in.ArchivoImagenCommand;
import co.edu.corhuila.rutasur.catalogo.application.port.out.AlmacenamientoImagenPuertoSalida;
import co.edu.corhuila.rutasur.catalogo.domain.exception.ExcepcionReglaNegocio;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Almacenamiento local configurable para desarrollo; no expone rutas físicas en la base de datos. */
@Component
@RequiredArgsConstructor
public class AlmacenamientoImagenLocalAdaptador implements AlmacenamientoImagenPuertoSalida {
    @Value("${app.archivos.directorio-imagenes}")
    private String directorioImagenes;

    @Override
    public String guardar(UUID experienciaId, UUID imagenId, ArchivoImagenCommand archivo) {
        String extension = extensionSegura(archivo.nombreOriginal(), archivo.tipoContenido());
        String nombre = imagenId + extension;
        Path carpeta = raiz().resolve(experienciaId.toString()).normalize();
        Path destino = carpeta.resolve(nombre).normalize();
        try {
            Files.createDirectories(carpeta);
            Files.write(destino, archivo.contenido());
            return "/archivos/experiencias/" + experienciaId + "/" + nombre;
        } catch (IOException error) {
            throw new IllegalStateException("No fue posible almacenar la imagen localmente.", error);
        }
    }

    @Override
    public void eliminar(String ruta) {
        try { Files.deleteIfExists(resolver(ruta)); }
        catch (IOException error) { throw new IllegalStateException("No fue posible eliminar la imagen local.", error); }
    }

    /** Obtiene una ruta validada para servir el archivo sin permitir traversal. */
    public Path resolver(String ruta) {
        String prefijo = "/archivos/experiencias/";
        if (ruta == null || !ruta.startsWith(prefijo)) throw new ExcepcionReglaNegocio("imagen.ruta.invalida");
        Path resultado = raiz().resolve(ruta.substring(prefijo.length())).normalize();
        if (!resultado.startsWith(raiz())) throw new ExcepcionReglaNegocio("imagen.ruta.invalida");
        return resultado;
    }

    private Path raiz() { return Path.of(directorioImagenes).toAbsolutePath().normalize(); }
    private String extensionSegura(String nombre, String tipo) {
        if (tipo == null || !tipo.toLowerCase(Locale.ROOT).startsWith("image/")) throw new ExcepcionReglaNegocio("imagen.tipo.invalido");
        String extension = nombre == null ? "" : nombre.replaceAll("^.*(\\\\.[^.]+)$", "$1").toLowerCase(Locale.ROOT);
        return extension.matches("\\\\.(jpg|jpeg|png|webp|gif)") ? extension : ".bin";
    }
}
