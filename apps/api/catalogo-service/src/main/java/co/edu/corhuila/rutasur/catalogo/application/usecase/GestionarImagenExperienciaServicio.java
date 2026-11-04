package co.edu.corhuila.rutasur.catalogo.application.usecase;

import co.edu.corhuila.rutasur.catalogo.application.port.in.ActualizarImagenExperienciaCommand;
import co.edu.corhuila.rutasur.catalogo.application.port.in.ArchivoImagenCommand;
import co.edu.corhuila.rutasur.catalogo.application.port.in.GestionarImagenExperienciaUseCase;
import co.edu.corhuila.rutasur.catalogo.application.port.out.AlmacenamientoImagenPuertoSalida;
import co.edu.corhuila.rutasur.catalogo.application.port.out.ExperienciaPuertoSalida;
import co.edu.corhuila.rutasur.catalogo.application.port.out.ImagenExperienciaPuertoSalida;
import co.edu.corhuila.rutasur.catalogo.domain.exception.ExcepcionReglaNegocio;
import co.edu.corhuila.rutasur.catalogo.domain.model.ImagenExperiencia;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/** Implementa de punta a punta el ciclo de vida de imágenes de una experiencia. */
@Service
@Slf4j
@RequiredArgsConstructor
public class GestionarImagenExperienciaServicio implements GestionarImagenExperienciaUseCase {
    private static final int TAMANO_MAXIMO_BYTES = 5 * 1024 * 1024;
    private final ExperienciaPuertoSalida experiencias;
    private final ImagenExperienciaPuertoSalida imagenes;
    private final AlmacenamientoImagenPuertoSalida almacenamiento;
    private final PublicadorCambiosCatalogo eventos;

    @Override @Transactional
    public ImagenExperiencia cargar(UUID experienciaId, ArchivoImagenCommand archivo, boolean principal, int orden, UUID correlacionId) {
        validarArchivo(archivo); validarOrden(orden); asegurarExperienciaActivaBloqueada(experienciaId);
        UUID imagenId = UUID.randomUUID();
        String ruta = almacenamiento.guardar(experienciaId, imagenId, archivo);
        registrarCompensacion(ruta);
        boolean seraPortada = principal || imagenes.listarActivasPorExperiencia(experienciaId).isEmpty();
        if (seraPortada) retirarPortadaActual(experienciaId, correlacionId);
        ImagenExperiencia imagen = imagenes.guardar(new ImagenExperiencia(imagenId, experienciaId, ruta, seraPortada, orden, true, OffsetDateTime.now(), null));
        eventos.imagen(imagen, correlacionId);
        log.info("correlationId={} servicio=catalogo casoUso=imagen operacion=cargar experienciaId={} imagenId={}", correlacionId, experienciaId, imagenId);
        return imagen;
    }

    @Override @Transactional(readOnly = true)
    public List<ImagenExperiencia> listar(UUID experienciaId) { asegurarExperienciaActiva(experienciaId); return imagenes.listarPorExperiencia(experienciaId); }

    @Override @Transactional
    public ImagenExperiencia actualizar(UUID experienciaId, UUID imagenId, ActualizarImagenExperienciaCommand comando) {
        validarOrdenOpcional(comando.orden()); asegurarExperienciaActivaBloqueada(experienciaId);
        ImagenExperiencia actual = obtenerActivaDeExperiencia(experienciaId, imagenId);
        boolean seraPortada = comando.principal() == null ? actual.principal() : comando.principal();
        if (seraPortada) retirarPortadaActual(experienciaId, comando.correlacionId());
        ImagenExperiencia actualizada = imagenes.guardar(new ImagenExperiencia(actual.id(), actual.experienciaId(), actual.ruta(), seraPortada,
                comando.orden() == null ? actual.orden() : comando.orden(), true, actual.creadoEn(), OffsetDateTime.now()));
        eventos.imagen(actualizada, comando.correlacionId());
        log.info("correlationId={} servicio=catalogo casoUso=imagen operacion=actualizar experienciaId={} imagenId={}", comando.correlacionId(), experienciaId, imagenId);
        return actualizada;
    }

    @Override @Transactional
    public void inactivar(UUID experienciaId, UUID imagenId, UUID correlacionId) {
        asegurarExperienciaActivaBloqueada(experienciaId);
        ImagenExperiencia actual = obtenerActivaDeExperiencia(experienciaId, imagenId);
        ImagenExperiencia inactiva = imagenes.guardar(new ImagenExperiencia(actual.id(), actual.experienciaId(), actual.ruta(), false, actual.orden(), false, actual.creadoEn(), OffsetDateTime.now()));
        eventos.imagen(inactiva, correlacionId);
        if (actual.principal()) imagenes.listarActivasPorExperiencia(experienciaId).stream().filter(imagen -> !imagen.id().equals(imagenId)).findFirst().ifPresent(siguiente -> {
            ImagenExperiencia portada = imagenes.guardar(new ImagenExperiencia(siguiente.id(), siguiente.experienciaId(), siguiente.ruta(), true, siguiente.orden(), true, siguiente.creadoEn(), OffsetDateTime.now()));
            eventos.imagen(portada, correlacionId);
        });
        log.info("correlationId={} servicio=catalogo casoUso=imagen operacion=inactivar experienciaId={} imagenId={}", correlacionId, experienciaId, imagenId);
    }

    private void asegurarExperienciaActiva(UUID experienciaId) { experiencias.buscarPorId(experienciaId).filter(e -> e.activa()).orElseThrow(() -> new ExcepcionReglaNegocio("experiencia.no.encontrada")); }
    private void asegurarExperienciaActivaBloqueada(UUID experienciaId) { experiencias.bloquearPorId(experienciaId).filter(e -> e.activa()).orElseThrow(() -> new ExcepcionReglaNegocio("experiencia.no.encontrada")); }
    private ImagenExperiencia obtenerActivaDeExperiencia(UUID experienciaId, UUID imagenId) { return imagenes.buscarPorId(imagenId).filter(imagen -> imagen.activa() && imagen.experienciaId().equals(experienciaId)).orElseThrow(() -> new ExcepcionReglaNegocio("imagen.no.encontrada")); }
    private void validarArchivo(ArchivoImagenCommand archivo) { if (archivo == null || archivo.contenido() == null || archivo.contenido().length == 0) throw new ExcepcionReglaNegocio("imagen.archivo.obligatorio"); if (archivo.contenido().length > TAMANO_MAXIMO_BYTES) throw new ExcepcionReglaNegocio("imagen.tamano.invalido"); }
    private void validarOrden(int orden) { if (orden < 0) throw new ExcepcionReglaNegocio("imagen.orden.invalido"); }
    private void validarOrdenOpcional(Integer orden) { if (orden != null) validarOrden(orden); }
    /** Sincroniza también el retiro de portada para que la proyección eventual no conserve dos portadas. */
    private void retirarPortadaActual(UUID experienciaId, UUID correlacionId) {
        List<ImagenExperiencia> anteriores = imagenes.listarActivasPorExperiencia(experienciaId).stream().filter(ImagenExperiencia::principal).toList();
        imagenes.quitarPortadaActiva(experienciaId);
        anteriores.forEach(anterior -> eventos.imagen(new ImagenExperiencia(anterior.id(), anterior.experienciaId(), anterior.ruta(), false, anterior.orden(), true, anterior.creadoEn(), OffsetDateTime.now()), correlacionId));
    }
    private void registrarCompensacion(String ruta) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) return;
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override public void afterCompletion(int estado) { if (estado != STATUS_COMMITTED) almacenamiento.eliminar(ruta); }
        });
    }
}
