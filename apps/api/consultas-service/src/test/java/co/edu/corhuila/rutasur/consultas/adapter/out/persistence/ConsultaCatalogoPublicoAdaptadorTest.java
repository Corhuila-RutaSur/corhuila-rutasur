package co.edu.corhuila.rutasur.consultas.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.time.*;
import java.util.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.*;
import org.mockito.junit.jupiter.*;

/**
 * Verifica el detalle público compuesto desde las proyecciones locales.
 */
@ExtendWith(MockitoExtension.class)
class ConsultaCatalogoPublicoAdaptadorTest {
    @Mock
    ExperienciaProyeccionRepositorio experiencias;
    @Mock
    ServicioProyeccionRepositorio servicios;
    @Mock
    ActividadProyeccionRepositorio actividades;
    @Mock
    SalidaProyeccionRepositorio salidas;
    @Mock
    ImagenExperienciaProyeccionRepositorio imagenes;
    @Mock
    ProgramacionServicioProyeccionRepositorio serviciosProgramados;
    @Mock
    ProgramacionActividadProyeccionRepositorio actividadesProgramadas;
    @InjectMocks
    ConsultaCatalogoPublicoAdaptador consulta;

    @Test
    void debeComponerDetalleConElementosProyectados() {
        UUID id = UUID.randomUUID();
        var e = new ExperienciaProyeccionEntidad();
        e.setId(id);
        e.setNombre("Tatacoa");
        e.setDestino("Huila");
        e.setDescripcion("Cielo");
        e.setActiva(true);
        var s = new ServicioProyeccionEntidad();
        s.setId(UUID.randomUUID());
        s.setTipo("TRANSPORTE");
        s.setNombre("Traslado");
        var a = new ActividadProyeccionEntidad();
        a.setId(UUID.randomUUID());
        a.setNombre("Estrellas");
        a.setDescripcion("Observación");
        a.setDuracionHoras(2);
        var salida = new SalidaProyeccionEntidad();
        salida.setId(UUID.randomUUID());
        salida.setExperienciaId(id);
        salida.setFechaHoraInicio(OffsetDateTime.now().plusDays(2));
        salida.setCuposDisponibles(5);
        salida.setEstado("PROGRAMADA");
        when(experiencias.findById(id)).thenReturn(Optional.of(e));
        when(servicios.findAllByExperienciaIdAndActivoTrue(id)).thenReturn(List.of(s));
        when(actividades.findAllByExperienciaIdAndActivaTrue(id)).thenReturn(List.of(a));
        when(salidas.findAllByExperienciaIdAndEstadoAndFechaHoraInicioAfterOrderByFechaHoraInicioAsc(eq(id), eq("PROGRAMADA"), any())).thenReturn(List.of(salida));
        when(imagenes.findFirstByExperienciaIdAndActivaTrueAndPrincipalTrue(id)).thenReturn(Optional.empty());
        when(imagenes.findAllByExperienciaIdAndActivaTrueOrderByOrdenAsc(id)).thenReturn(List.of());
        var resultado = consulta.consultar(id);
        assertThat(resultado.servicios()).hasSize(1);
        assertThat(resultado.actividades()).hasSize(1);
        assertThat(resultado.salidas()).hasSize(1);
    }

    @Test
    void debeExponerDisponibilidadConOpcionesSeleccionables() {
        UUID salidaId = UUID.randomUUID();
        var salida = new SalidaProyeccionEntidad();
        salida.setId(salidaId); salida.setExperienciaId(UUID.randomUUID()); salida.setFechaHoraInicio(OffsetDateTime.now().plusDays(1)); salida.setCuposDisponibles(7); salida.setEstado("PROGRAMADA");
        var servicio = new ProgramacionServicioProyeccionEntidad();
        servicio.setId(UUID.randomUUID()); servicio.setServicioIdExterno(UUID.randomUUID()); servicio.setNombre("Transporte"); servicio.setPrecio(new java.math.BigDecimal("45000")); servicio.setCuposDisponibles(7); servicio.setActivo(true);
        var actividad = new ProgramacionActividadProyeccionEntidad();
        actividad.setId(UUID.randomUUID()); actividad.setActividadIdExterna(UUID.randomUUID()); actividad.setNombre("Fogata"); actividad.setPrecio(new java.math.BigDecimal("30000")); actividad.setActivo(true);
        when(salidas.findById(salidaId)).thenReturn(Optional.of(salida));
        when(serviciosProgramados.findAllBySalidaIdAndActivoTrueOrderByNombreAsc(salidaId)).thenReturn(List.of(servicio));
        when(actividadesProgramadas.findAllBySalidaIdAndActivoTrueOrderByNombreAsc(salidaId)).thenReturn(List.of(actividad));

        var resultado = consulta.consultarDisponibilidad(salidaId);

        assertThat(resultado.cuposDisponibles()).isEqualTo(7);
        assertThat(resultado.servicios()).hasSize(1);
        assertThat(resultado.actividades()).hasSize(1);
    }
}
