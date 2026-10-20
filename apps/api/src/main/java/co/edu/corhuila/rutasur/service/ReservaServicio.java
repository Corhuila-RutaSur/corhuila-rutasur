package co.edu.corhuila.rutasur.service;

import co.edu.corhuila.rutasur.dto.request.ParticipanteReservaRequest;
import co.edu.corhuila.rutasur.dto.request.ReservaActualizacionRequest;
import co.edu.corhuila.rutasur.dto.request.ReservaCreacionRequest;
import co.edu.corhuila.rutasur.dto.response.ReservaResponse;
import co.edu.corhuila.rutasur.entity.DetalleParticipante;
import co.edu.corhuila.rutasur.entity.DetalleReserva;
import co.edu.corhuila.rutasur.entity.EstadoReserva;
import co.edu.corhuila.rutasur.entity.ParticipanteReserva;
import co.edu.corhuila.rutasur.entity.Reserva;
import co.edu.corhuila.rutasur.exception.ExcepcionReglaNegocio;
import co.edu.corhuila.rutasur.mapper.ReservaMapper;
import co.edu.corhuila.rutasur.repository.DetalleParticipanteRepositorio;
import co.edu.corhuila.rutasur.repository.DetalleReservaRepositorio;
import co.edu.corhuila.rutasur.repository.ParticipanteReservaRepositorio;
import co.edu.corhuila.rutasur.repository.ProgramacionReservaProyeccion;
import co.edu.corhuila.rutasur.repository.ReservaRepositorio;
import co.edu.corhuila.rutasur.repository.SalidaReservaProyeccion;
import co.edu.corhuila.rutasur.repository.UsuarioRepositorio;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** Implementa el ciclo de vida inicial de reservas configurables. */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReservaServicio {
    private final ReservaRepositorio reservaRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;
    private final ParticipanteReservaRepositorio participanteRepositorio;
    private final DetalleReservaRepositorio detalleRepositorio;
    private final DetalleParticipanteRepositorio detalleParticipanteRepositorio;
    private final ReservaMapper reservaMapper;

    /** Crea una reserva pendiente, descuenta cupos y congela los precios vigentes. */
    @Transactional
    public ReservaResponse crear(ReservaCreacionRequest solicitud) {
        log.info("evento=reserva_creacion_inicio usuarioId={} salidaId={} participantes={} servicios={}", solicitud.usuarioId(), solicitud.salidaExperienciaId(), solicitud.participantes().size(), solicitud.programacionesServicioIds().size());
        validarUsuarioActivo(solicitud.usuarioId());
        validarResponsable(solicitud.participantes());
        Map<Long, Long> cantidades = contarServicios(solicitud.programacionesServicioIds(), solicitud.participantes().size());
        SalidaReservaProyeccion salida = bloquearSalidaAbierta(solicitud.salidaExperienciaId());
        validarCuposSalida(salida, solicitud.participantes().size());
        Map<Long, ProgramacionReservaProyeccion> programaciones = bloquearYValidarProgramaciones(
                solicitud.salidaExperienciaId(), cantidades);
        log.info("evento=reserva_disponibilidad_validada salidaId={} participantes={}", salida.getId(), solicitud.participantes().size());
        descontarCupos(salida.getId(), cantidades, programaciones, solicitud.participantes().size());
        Reserva reserva = guardarCabecera(solicitud, cantidades, programaciones);
        guardarParticipantesYAsignaciones(reserva, solicitud.participantes(), solicitud.programacionesServicioIds(), cantidades, programaciones);
        log.info("evento=reserva_creacion_exitosa reservaId={} codigo={} participantes={}", reserva.getId(), reserva.getCodigo(), solicitud.participantes().size());
        return reservaMapper.aRespuesta(reserva);
    }

    /** Consulta una reserva que no haya sido cancelada. */
    @Transactional(readOnly = true)
    public ReservaResponse consultarPorId(Long id) {
        log.info("evento=reserva_consulta_inicio reservaId={}", id);
        return reservaMapper.aRespuesta(obtenerReservaVigente(id));
    }

    /** Lista las reservas de un usuario, incluidos sus estados historicos. */
    @Transactional(readOnly = true)
    public List<ReservaResponse> consultarPorUsuario(Long usuarioId) {
        log.info("evento=reservas_usuario_consulta_inicio usuarioId={}", usuarioId);
        return reservaRepositorio.findByUsuarioIdOrderByFechaReservaDesc(usuarioId).stream()
                .map(reservaMapper::aRespuesta).toList();
    }

    /** Actualiza observaciones de una reserva mientras se encuentra pendiente. */
    @Transactional
    public ReservaResponse actualizar(Long id, ReservaActualizacionRequest solicitud) {
        log.info("evento=reserva_actualizacion_inicio reservaId={}", id);
        Reserva reserva = obtenerReservaVigente(id);
        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            throw new ExcepcionReglaNegocio("reserva.actualizacion.no_permitida");
        }
        reserva.setObservaciones(solicitud.observaciones().trim());
        log.info("evento=reserva_actualizacion_exitosa reservaId={}", id);
        return reservaMapper.aRespuesta(reserva);
    }

    /** Cancela lógicamente una reserva y restituye sus cupos disponibles. */
    @Transactional
    public void cancelar(Long id) {
        log.info("evento=reserva_cancelacion_inicio reservaId={}", id);
        Reserva reserva = obtenerReservaVigente(id);
        if (reserva.getEstado() == EstadoReserva.FINALIZADA) {
            throw new ExcepcionReglaNegocio("reserva.cancelacion.no_permitida");
        }
        restaurarCupos(reserva);
        reserva.setEstado(EstadoReserva.CANCELADA);
        log.info("evento=reserva_cancelacion_exitosa reservaId={}", id);
    }

    /** Valida que la cuenta compradora exista y permanezca activa. */
    private void validarUsuarioActivo(Long usuarioId) {
        if (usuarioRepositorio.findByIdAndActivoTrue(usuarioId).isEmpty()) {
            throw new ExcepcionReglaNegocio("usuario.no.encontrado");
        }
    }

    /** Bloquea la salida para evitar sobreventa durante la transacción. */
    private SalidaReservaProyeccion bloquearSalidaAbierta(Long salidaId) {
        SalidaReservaProyeccion salida = reservaRepositorio.bloquearSalida(salidaId)
                .orElseThrow(() -> new ExcepcionReglaNegocio("reserva.salida.no_encontrada"));
        if (!"ABIERTA".equals(salida.getEstado())) {
            throw new ExcepcionReglaNegocio("reserva.salida.no_disponible");
        }
        return salida;
    }

    /** Verifica que exista exactamente un participante responsable. */
    private void validarResponsable(List<ParticipanteReservaRequest> participantes) {
        long responsables = participantes.stream().filter(ParticipanteReservaRequest::responsable).count();
        if (responsables != 1) {
            throw new ExcepcionReglaNegocio("reserva.responsable.invalido");
        }
    }

    /** Cuenta cuántos cupos de cada servicio fueron seleccionados. */
    private Map<Long, Long> contarServicios(List<Long> programacionesServicioIds, int cantidadParticipantes) {
        if (programacionesServicioIds.stream().distinct().count() != programacionesServicioIds.size()) {
            throw new ExcepcionReglaNegocio("reserva.servicio.duplicado");
        }
        return programacionesServicioIds.stream().collect(Collectors.toMap(
                Function.identity(), servicioId -> (long) cantidadParticipantes));
    }

    /** Verifica disponibilidad general de la salida. */
    private void validarCuposSalida(SalidaReservaProyeccion salida, int cantidadParticipantes) {
        if (salida.getCuposDisponibles() < cantidadParticipantes) {
            throw new ExcepcionReglaNegocio("reserva.cupos.insuficientes");
        }
    }

    /** Bloquea y valida los servicios pertenecientes a la misma salida. */
    private Map<Long, ProgramacionReservaProyeccion> bloquearYValidarProgramaciones(
            Long salidaId, Map<Long, Long> cantidades) {
        List<ProgramacionReservaProyeccion> programaciones = reservaRepositorio.bloquearProgramaciones(
                salidaId, List.copyOf(cantidades.keySet()));
        if (programaciones.size() != cantidades.size()) {
            throw new ExcepcionReglaNegocio("reserva.servicio.invalido");
        }
        Map<Long, ProgramacionReservaProyeccion> resultado = programaciones.stream()
                .collect(Collectors.toMap(ProgramacionReservaProyeccion::getId, Function.identity()));
        for (Map.Entry<Long, Long> cantidad : cantidades.entrySet()) {
            ProgramacionReservaProyeccion programacion = resultado.get(cantidad.getKey());
            if (!Boolean.TRUE.equals(programacion.getActivo())
                    || (programacion.getCuposDisponibles() != null && programacion.getCuposDisponibles() < cantidad.getValue())) {
                throw new ExcepcionReglaNegocio("reserva.cupos.insuficientes");
            }
        }
        return resultado;
    }

    /** Descuenta cupos generales y específicos bajo la misma transacción. */
    private void descontarCupos(Long salidaId, Map<Long, Long> cantidades,
            Map<Long, ProgramacionReservaProyeccion> programaciones, int participantes) {
        if (reservaRepositorio.descontarCuposSalida(salidaId, participantes) == 0) {
            throw new ExcepcionReglaNegocio("reserva.cupos.insuficientes");
        }
        for (Map.Entry<Long, Long> cantidad : cantidades.entrySet()) {
            if (programaciones.get(cantidad.getKey()).getCuposDisponibles() != null
                    && reservaRepositorio.descontarCuposServicio(cantidad.getKey(), cantidad.getValue().intValue()) == 0) {
                throw new ExcepcionReglaNegocio("reserva.cupos.insuficientes");
            }
        }
    }

    /** Crea y persiste la cabecera con precios congelados de los detalles. */
    private Reserva guardarCabecera(ReservaCreacionRequest solicitud, Map<Long, Long> cantidades,
            Map<Long, ProgramacionReservaProyeccion> programaciones) {
        BigDecimal subtotal = cantidades.entrySet().stream().map(cantidad -> programaciones.get(cantidad.getKey())
                .getPrecioUnitario().multiply(BigDecimal.valueOf(cantidad.getValue()))).reduce(BigDecimal.ZERO, BigDecimal::add);
        Reserva reserva = new Reserva();
        reserva.setCodigo("RS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        reserva.setUsuarioId(solicitud.usuarioId());
        reserva.setSalidaExperienciaId(solicitud.salidaExperienciaId());
        reserva.setSubtotal(subtotal);
        reserva.setTotal(subtotal);
        reserva.setObservaciones(solicitud.observaciones());
        return reservaRepositorio.save(reserva);
    }

    /** Persiste participantes, detalles y las asignaciones individuales de servicios. */
    private void guardarParticipantesYAsignaciones(Reserva reserva, List<ParticipanteReservaRequest> solicitudes,
            List<Long> programacionesServicioIds, Map<Long, Long> cantidades,
            Map<Long, ProgramacionReservaProyeccion> programaciones) {
        Map<Long, DetalleReserva> detalles = guardarDetalles(reserva, cantidades, programaciones);
        for (ParticipanteReservaRequest solicitud : solicitudes) {
            ParticipanteReserva participante = crearParticipante(reserva.getId(), solicitud);
            participanteRepositorio.save(participante);
            for (Long programacionId : programacionesServicioIds) {
                DetalleParticipante asignacion = new DetalleParticipante();
                asignacion.setDetalleReservaId(detalles.get(programacionId).getId());
                asignacion.setParticipanteReservaId(participante.getId());
                detalleParticipanteRepositorio.save(asignacion);
            }
        }
    }

    /** Persiste los detalles de compra calculados para cada servicio. */
    private Map<Long, DetalleReserva> guardarDetalles(Reserva reserva, Map<Long, Long> cantidades,
            Map<Long, ProgramacionReservaProyeccion> programaciones) {
        return cantidades.entrySet().stream().map(cantidad -> {
            DetalleReserva detalle = new DetalleReserva();
            detalle.setReservaId(reserva.getId());
            detalle.setProgramacionServicioId(cantidad.getKey());
            detalle.setCantidad(cantidad.getValue().intValue());
            detalle.setPrecioUnitario(programaciones.get(cantidad.getKey()).getPrecioUnitario());
            detalle.setSubtotal(detalle.getPrecioUnitario().multiply(BigDecimal.valueOf(detalle.getCantidad())));
            detalleRepositorio.save(detalle);
            return detalle;
        }).collect(Collectors.toMap(DetalleReserva::getProgramacionServicioId, Function.identity()));
    }

    /** Construye la entidad participante desde el contrato validado. */
    private ParticipanteReserva crearParticipante(Long reservaId, ParticipanteReservaRequest solicitud) {
        ParticipanteReserva participante = new ParticipanteReserva();
        participante.setReservaId(reservaId); participante.setTipoDocumento(solicitud.tipoDocumento());
        participante.setNumeroDocumento(solicitud.numeroDocumento()); participante.setNombres(solicitud.nombres());
        participante.setApellidos(solicitud.apellidos()); participante.setCorreo(solicitud.correo());
        participante.setTelefono(solicitud.telefono()); participante.setResponsable(solicitud.responsable());
        return participante;
    }

    /** Obtiene una reserva no cancelada o informa su ausencia. */
    private Reserva obtenerReservaVigente(Long id) {
        return reservaRepositorio.findByIdAndEstadoNot(id, EstadoReserva.CANCELADA)
                .orElseThrow(() -> new ExcepcionReglaNegocio("reserva.no_encontrada"));
    }

    /** Restaura los cupos consumidos por una reserva que se cancela. */
    private void restaurarCupos(Reserva reserva) {
        List<DetalleReserva> detalles = detalleRepositorio.findByReservaId(reserva.getId());
        reservaRepositorio.restaurarCuposSalida(reserva.getSalidaExperienciaId(),
                participanteRepositorio.findByReservaId(reserva.getId()).size());
        detalles.forEach(detalle -> reservaRepositorio.restaurarCuposServicio(
                detalle.getProgramacionServicioId(), detalle.getCantidad()));
    }
}
