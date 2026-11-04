package co.edu.corhuila.rutasur.consultas.adapter.in.http;

import co.edu.corhuila.rutasur.consultas.adapter.out.persistence.ConsultaCatalogoPublicoAdaptador;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Entrada pública de lectura: no ejecuta comandos ni consulta otros microservicios.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/public/experiencias")
public class CatalogoPublicoControlador {
    private final ConsultaCatalogoPublicoAdaptador consultas;

    @GetMapping
    public ResponseEntity<RespuestaGeneral<?>> listar() {
        return ResponseEntity.ok(RespuestaGeneral.exitosa("200", "catalogo.consultado", "Catálogo público consultado correctamente.", consultas.listar()));
    }

    @GetMapping("/{experienciaId}")
    public ResponseEntity<RespuestaGeneral<?>> detalle(@PathVariable UUID experienciaId) {
        return ResponseEntity.ok(RespuestaGeneral.exitosa("200", "experiencia.consultada", "Experiencia consultada correctamente.", consultas.consultar(experienciaId)));
    }

    @GetMapping("/salidas/{salidaId}/disponibilidad")
    public ResponseEntity<RespuestaGeneral<?>> disponibilidadSalida(@PathVariable UUID salidaId) {
        return ResponseEntity.ok(RespuestaGeneral.exitosa("200", "salida.disponibilidad.consultada", "Disponibilidad de salida consultada correctamente.", consultas.consultarDisponibilidad(salidaId)));
    }
}
