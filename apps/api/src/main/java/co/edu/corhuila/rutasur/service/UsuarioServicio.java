package co.edu.corhuila.rutasur.service;

import co.edu.corhuila.rutasur.dto.request.UsuarioActualizacionRequest;
import co.edu.corhuila.rutasur.dto.request.UsuarioRegistroRequest;
import co.edu.corhuila.rutasur.dto.response.UsuarioResponse;
import co.edu.corhuila.rutasur.entity.Usuario;
import co.edu.corhuila.rutasur.entity.UsuarioTipo;
import co.edu.corhuila.rutasur.entity.TipoUsuario;
import co.edu.corhuila.rutasur.exception.ExcepcionReglaNegocio;
import co.edu.corhuila.rutasur.mapper.UsuarioMapper;
import co.edu.corhuila.rutasur.repository.UsuarioRepositorio;
import co.edu.corhuila.rutasur.repository.TipoUsuarioRepositorio;
import co.edu.corhuila.rutasur.repository.UsuarioTipoRepositorio;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementa los casos de uso de registro y administracion logica de usuarios.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioServicio {

    private static final String ROL_CLIENTE = "CLIENTE";

    private final UsuarioRepositorio usuarioRepositorio;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder codificadorContrasenias;
    private final TipoUsuarioRepositorio tipoUsuarioRepositorio;
    private final UsuarioTipoRepositorio usuarioTipoRepositorio;

    /**
     * Registra una cuenta activa con contraseña codificada mediante BCrypt.
     *
     * @param solicitud datos validados de registro.
     * @return usuario creado sin datos sensibles.
     * @throws ExcepcionReglaNegocio si el documento, correo o telefono ya estan registrados.
     */
    @Transactional
    public UsuarioResponse registrar(UsuarioRegistroRequest solicitud) {
        log.info("evento=usuario_registro_inicio");
        Usuario usuario = usuarioMapper.aEntidad(solicitud);
        normalizarDatos(usuario);
        validarUnicidad(usuario, null);
        usuario.setPasswordHash(codificadorContrasenias.encode(solicitud.contrasenia()));
        usuario.setActivo(true);
        Usuario usuarioCreado = usuarioRepositorio.save(usuario);
        asignarRolCliente(usuarioCreado);
        log.info("evento=usuario_registro_exitoso usuarioId={} rol={}", usuarioCreado.getId(), ROL_CLIENTE);
        return usuarioMapper.aRespuesta(usuarioCreado);
    }

    /** Crea una cuenta desde administración con los roles definidos por el administrador. */
    @Transactional
    public UsuarioResponse registrarAdministrativo(UsuarioRegistroRequest solicitud, List<String> nombresRoles) {
        log.info("evento=usuario_administrativo_registro_inicio cantidadRoles={}", nombresRoles.size());
        Usuario usuario = usuarioMapper.aEntidad(solicitud);
        normalizarDatos(usuario);
        validarUnicidad(usuario, null);
        usuario.setPasswordHash(codificadorContrasenias.encode(solicitud.contrasenia()));
        usuario.setActivo(true);
        Usuario creado = usuarioRepositorio.save(usuario);
        asignarRoles(creado, nombresRoles);
        log.info("evento=usuario_administrativo_registro_exitoso usuarioId={}", creado.getId());
        return usuarioMapper.aRespuesta(creado);
    }

    /**
     * Consulta una cuenta activa por identificador.
     *
     * @param id identificador tecnico del usuario.
     * @return datos seguros del usuario.
     * @throws ExcepcionReglaNegocio si no existe una cuenta activa con el identificador.
     */
    @Transactional(readOnly = true)
    public UsuarioResponse consultarPorId(Long id) {
        log.info("evento=usuario_consulta_inicio usuarioId={}", id);
        return usuarioMapper.aRespuesta(obtenerUsuarioActivo(id));
    }

    /**
     * Lista cuentas activas con una proyeccion que evita leer el hash de contrasenia.
     *
     * @return usuarios activos ordenados por nombres y apellidos.
     */
    @Transactional(readOnly = true)
    public List<UsuarioResponse> consultarActivos() {
        log.info("evento=usuarios_consulta_activos_inicio");
        List<UsuarioResponse> usuarios = usuarioRepositorio.findByActivoTrueOrderByNombresAscApellidosAsc().stream()
                .map(usuarioMapper::aRespuesta)
                .toList();
        log.info("evento=usuarios_consulta_activos_exitoso cantidad={}", usuarios.size());
        return usuarios;
    }

    /**
     * Actualiza los datos de perfil permitidos de una cuenta activa.
     *
     * @param id identificador tecnico del usuario.
     * @param solicitud datos completos y validados de actualizacion.
     * @return usuario actualizado sin datos sensibles.
     * @throws ExcepcionReglaNegocio si no existe el usuario o se repite un dato unico.
     */
    @Transactional
    public UsuarioResponse actualizar(Long id, UsuarioActualizacionRequest solicitud) {
        log.info("evento=usuario_actualizacion_inicio usuarioId={}", id);
        Usuario usuario = obtenerUsuarioActivo(id);
        usuarioMapper.actualizarEntidad(solicitud, usuario);
        normalizarDatos(usuario);
        validarUnicidad(usuario, id);
        log.info("evento=usuario_actualizacion_exitosa usuarioId={}", id);
        return usuarioMapper.aRespuesta(usuario);
    }

    /**
     * Inactiva una cuenta sin borrar su historial ni sus relaciones persistentes.
     *
     * @param id identificador tecnico del usuario.
     * @throws ExcepcionReglaNegocio si no existe una cuenta activa con el identificador.
     */
    @Transactional
    public void eliminarLogicamente(Long id) {
        log.info("evento=usuario_inactivacion_inicio usuarioId={}", id);
        int registrosActualizados = usuarioRepositorio.desactivarPorId(id, OffsetDateTime.now());
        if (registrosActualizados == 0) {
            throw new ExcepcionReglaNegocio("usuario.no.encontrado");
        }
        log.info("evento=usuario_inactivacion_exitosa usuarioId={}", id);
    }

    /** Reemplaza los roles de una cuenta desde el módulo administrativo. */
    @Transactional
    public UsuarioResponse actualizarRoles(Long id, List<String> nombresRoles) {
        log.info("evento=usuario_roles_actualizacion_inicio usuarioId={} cantidadRoles={}", id, nombresRoles.size());
        Usuario usuario = obtenerUsuarioActivo(id);
        usuarioTipoRepositorio.deleteByUsuarioId(usuario.getId());
        asignarRoles(usuario, nombresRoles);
        log.info("evento=usuario_roles_actualizacion_exitosa usuarioId={}", id);
        return usuarioMapper.aRespuesta(usuario);
    }

    /**
     * Obtiene la entidad que sera leida o modificada por un caso de uso.
     *
     * @param id identificador tecnico solicitado.
     * @return entidad activa administrada por JPA.
     * @throws ExcepcionReglaNegocio si no existe una cuenta activa con el identificador.
     */
    private Usuario obtenerUsuarioActivo(Long id) {
        return usuarioRepositorio.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ExcepcionReglaNegocio("usuario.no.encontrado"));
    }

    /**
     * Comprueba las tres claves de negocio que deben ser exclusivas entre usuarios.
     *
     * @param usuario datos normalizados que se desean persistir.
     * @param idExcluido identificador a ignorar durante una actualizacion; nulo para registro.
     * @throws ExcepcionReglaNegocio si alguno de los datos ya se encuentra registrado.
     */
    private void validarUnicidad(Usuario usuario, Long idExcluido) {
        validarNumeroDocumento(usuario.getNumeroDocumento(), idExcluido);
        validarCorreo(usuario.getCorreo(), idExcluido);
        validarTelefono(usuario.getTelefono(), idExcluido);
    }

    /**
     * Verifica que el documento no este asociado a otra cuenta.
     *
     * @param numeroDocumento documento normalizado.
     * @param idExcluido usuario que se actualiza, si aplica.
     */
    private void validarNumeroDocumento(String numeroDocumento, Long idExcluido) {
        boolean existe = idExcluido == null
                ? usuarioRepositorio.existsByNumeroDocumento(numeroDocumento)
                : usuarioRepositorio.existsByNumeroDocumentoAndIdNot(numeroDocumento, idExcluido);
        lanzarSiDuplicado(existe, "usuario.numero_documento.duplicado");
    }

    /**
     * Verifica que el correo no este asociado a otra cuenta.
     *
     * @param correo correo normalizado.
     * @param idExcluido usuario que se actualiza, si aplica.
     */
    private void validarCorreo(String correo, Long idExcluido) {
        boolean existe = idExcluido == null
                ? usuarioRepositorio.existsByCorreoIgnoreCase(correo)
                : usuarioRepositorio.existsByCorreoIgnoreCaseAndIdNot(correo, idExcluido);
        lanzarSiDuplicado(existe, "usuario.correo.duplicado");
    }

    /**
     * Verifica que el telefono no este asociado a otra cuenta.
     *
     * @param telefono telefono normalizado.
     * @param idExcluido usuario que se actualiza, si aplica.
     */
    private void validarTelefono(String telefono, Long idExcluido) {
        boolean existe = idExcluido == null
                ? usuarioRepositorio.existsByTelefono(telefono)
                : usuarioRepositorio.existsByTelefonoAndIdNot(telefono, idExcluido);
        lanzarSiDuplicado(existe, "usuario.telefono.duplicado");
    }

    /**
     * Lanza un error funcional cuando una consulta de unicidad encontro coincidencias.
     *
     * @param existe indica si se encontro el dato.
     * @param codigo clave de mensaje asociada al dato duplicado.
     */
    private void lanzarSiDuplicado(boolean existe, String codigo) {
        if (existe) {
            throw new ExcepcionReglaNegocio(codigo);
        }
    }

    /**
     * Uniforma valores de negocio antes de validar sus restricciones de unicidad.
     *
     * @param usuario entidad cuyos datos se normalizan.
     */
    private void normalizarDatos(Usuario usuario) {
        usuario.setTipoDocumento(usuario.getTipoDocumento().trim().toUpperCase(Locale.ROOT));
        usuario.setNumeroDocumento(usuario.getNumeroDocumento().trim().toUpperCase(Locale.ROOT));
        usuario.setNombres(usuario.getNombres().trim());
        usuario.setApellidos(usuario.getApellidos().trim());
        usuario.setCorreo(usuario.getCorreo().trim().toLowerCase(Locale.ROOT));
        usuario.setTelefono(usuario.getTelefono().trim());
    }

    /**
     * Asigna el rol CLIENTE definido en los datos semilla a una cuenta nueva.
     *
     * @param usuario usuario persistido que recibira el rol por defecto.
     * @throws ExcepcionReglaNegocio si el catalogo no contiene un rol CLIENTE activo.
     */
    private void asignarRolCliente(Usuario usuario) {
        TipoUsuario rolCliente = tipoUsuarioRepositorio.findByNombreAndActivoTrue(ROL_CLIENTE)
                .orElseThrow(() -> new ExcepcionReglaNegocio("usuario.rol_cliente.no_encontrado"));
        usuarioTipoRepositorio.save(new UsuarioTipo(usuario, rolCliente));
    }

    /** Valida y asigna la colección de roles de administración a una cuenta. */
    private void asignarRoles(Usuario usuario, List<String> nombresRoles) {
        List<String> rolesNormalizados = nombresRoles.stream().map(nombre -> nombre.trim().toUpperCase(Locale.ROOT)).distinct().toList();
        List<TipoUsuario> roles = tipoUsuarioRepositorio.findByNombreInAndActivoTrue(rolesNormalizados);
        if (roles.size() != rolesNormalizados.size()) {
            throw new ExcepcionReglaNegocio("usuario.rol.no_encontrado");
        }
        usuarioTipoRepositorio.saveAll(roles.stream().map(rol -> new UsuarioTipo(usuario, rol)).toList());
    }
}
