package co.edu.corhuila.rutasur.catalogo.application.port.in;

/** Contenido binario recibido por HTTP, aislado de Spring para el caso de uso. */
public record ArchivoImagenCommand(String nombreOriginal, String tipoContenido, byte[] contenido) {
}
