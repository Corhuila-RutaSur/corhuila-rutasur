-- Spec V2: docs/specs/002-identidad-autorizacion-y-sesiones.md
-- Propietario exclusivo: identidad-service / rutasur_identidad_db.
-- Esta migración no contiene ni referencia objetos de otros microservicios.

CREATE
EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE usuario
(
    id               UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    tipo_documento   VARCHAR(20)  NOT NULL,
    numero_documento VARCHAR(30)  NOT NULL,
    nombres          VARCHAR(100) NOT NULL,
    apellidos        VARCHAR(100) NOT NULL,
    correo           VARCHAR(150) NOT NULL,
    telefono         VARCHAR(10)  NOT NULL,
    password_hash    VARCHAR(255) NOT NULL,
    activo           BOOLEAN      NOT NULL DEFAULT TRUE,
    creado_en        TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en   TIMESTAMPTZ,
    CONSTRAINT uk_usuario_numero_documento UNIQUE (numero_documento),
    CONSTRAINT uk_usuario_correo UNIQUE (correo),
    CONSTRAINT uk_usuario_telefono UNIQUE (telefono),
    CONSTRAINT ck_usuario_telefono_colombiano CHECK (telefono ~ '^3[0-9]{9}$'
)
    );

CREATE TABLE rol
(
    id          UUID PRIMARY KEY     DEFAULT gen_random_uuid(),
    nombre      VARCHAR(50) NOT NULL,
    descripcion VARCHAR(255),
    activo      BOOLEAN     NOT NULL DEFAULT TRUE,
    creado_en   TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_rol_nombre UNIQUE (nombre)
);

CREATE TABLE usuario_rol
(
    usuario_id              UUID        NOT NULL,
    rol_id                  UUID        NOT NULL,
    asignado_en             TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    asignado_por_usuario_id UUID,
    CONSTRAINT pk_usuario_rol PRIMARY KEY (usuario_id, rol_id),
    CONSTRAINT fk_usuario_rol_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id),
    CONSTRAINT fk_usuario_rol_rol FOREIGN KEY (rol_id) REFERENCES rol (id),
    CONSTRAINT fk_usuario_rol_asignado_por FOREIGN KEY (asignado_por_usuario_id) REFERENCES usuario (id)
);

CREATE TABLE sesion_autenticacion
(
    id             UUID PRIMARY KEY     DEFAULT gen_random_uuid(),
    usuario_id     UUID        NOT NULL,
    estado         VARCHAR(20) NOT NULL,
    ip_origen      VARCHAR(45),
    agente_usuario VARCHAR(500),
    iniciada_en    TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expira_en      TIMESTAMPTZ NOT NULL,
    invalidada_en  TIMESTAMPTZ,
    CONSTRAINT fk_sesion_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id),
    CONSTRAINT ck_sesion_estado CHECK (estado IN ('ACTIVA', 'INVALIDADA', 'EXPIRADA'))
);

CREATE TABLE refresh_token
(
    id                 UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    sesion_id          UUID         NOT NULL,
    token_hash         VARCHAR(255) NOT NULL,
    usado              BOOLEAN      NOT NULL DEFAULT FALSE,
    creado_en          TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expira_en          TIMESTAMPTZ  NOT NULL,
    usado_en           TIMESTAMPTZ,
    reemplazado_por_id UUID,
    CONSTRAINT uk_refresh_token_hash UNIQUE (token_hash),
    CONSTRAINT fk_refresh_sesion FOREIGN KEY (sesion_id) REFERENCES sesion_autenticacion (id),
    CONSTRAINT fk_refresh_reemplazado_por FOREIGN KEY (reemplazado_por_id) REFERENCES refresh_token (id)
);

CREATE TABLE access_token_revocado
(
    id          UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    sesion_id   UUID         NOT NULL,
    jti         VARCHAR(100) NOT NULL,
    expira_en   TIMESTAMPTZ  NOT NULL,
    revocado_en TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    motivo      VARCHAR(100),
    CONSTRAINT uk_access_token_revocado_jti UNIQUE (jti),
    CONSTRAINT fk_access_revocado_sesion FOREIGN KEY (sesion_id) REFERENCES sesion_autenticacion (id)
);

CREATE TABLE evento_autenticacion
(
    id             UUID PRIMARY KEY     DEFAULT gen_random_uuid(),
    usuario_id     UUID,
    tipo           VARCHAR(80) NOT NULL,
    detalle        JSONB,
    correlacion_id UUID,
    ocurrido_en    TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_evento_autenticacion_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id)
);

CREATE TABLE outbox_evento
(
    id                   UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    tipo                 VARCHAR(100) NOT NULL,
    version              INTEGER      NOT NULL DEFAULT 1,
    agregado_id          UUID         NOT NULL,
    correlacion_id       UUID,
    datos                JSONB        NOT NULL,
    ocurrido_en          TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    publicado_en         TIMESTAMPTZ,
    intentos_publicacion INTEGER      NOT NULL DEFAULT 0,
    ultimo_error         TEXT,
    CONSTRAINT ck_outbox_version_positiva CHECK (version > 0)
);

CREATE TABLE inbox_evento
(
    id_evento    UUID PRIMARY KEY,
    tipo         VARCHAR(100) NOT NULL,
    origen       VARCHAR(100) NOT NULL,
    recibido_en  TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    procesado_en TIMESTAMPTZ,
    estado       VARCHAR(20)  NOT NULL DEFAULT 'RECIBIDO',
    CONSTRAINT ck_inbox_estado CHECK (estado IN ('RECIBIDO', 'PROCESADO', 'FALLIDO'))
);

CREATE INDEX ix_usuario_activo ON usuario (activo);
CREATE INDEX ix_sesion_usuario_estado ON sesion_autenticacion (usuario_id, estado);
CREATE INDEX ix_sesion_expira_en ON sesion_autenticacion (expira_en);
CREATE INDEX ix_refresh_sesion ON refresh_token (sesion_id);
CREATE INDEX ix_refresh_expira_en ON refresh_token (expira_en);
CREATE INDEX ix_outbox_pendiente ON outbox_evento (publicado_en, ocurrido_en) WHERE publicado_en IS NULL;
CREATE INDEX ix_evento_autenticacion_usuario_fecha ON evento_autenticacion (usuario_id, ocurrido_en DESC);

INSERT INTO rol (nombre, descripcion)
VALUES ('CLIENTE', 'Usuario que explora, reserva y paga experiencias.'),
       ('ADMINISTRADOR', 'Usuario que administra identidades y catálogo.'),
       ('GUIA', 'Usuario operativo que acompaña una experiencia.'),
       ('CONDUCTOR', 'Usuario operativo que conduce servicios de transporte.') ON CONFLICT (nombre) DO NOTHING;

COMMENT
ON TABLE usuario IS 'Fuente de verdad de las identidades de RutaSur V2.';
COMMENT
ON COLUMN usuario.id IS 'Identificador interno UUID del usuario.';
COMMENT
ON COLUMN usuario.tipo_documento IS 'Tipo de documento de identidad informado por el usuario.';
COMMENT
ON COLUMN usuario.numero_documento IS 'Número de documento único del usuario.';
COMMENT
ON COLUMN usuario.nombres IS 'Nombres del usuario.';
COMMENT
ON COLUMN usuario.apellidos IS 'Apellidos del usuario.';
COMMENT
ON COLUMN usuario.correo IS 'Correo electrónico único usado para autenticación.';
COMMENT
ON COLUMN usuario.telefono IS 'Teléfono colombiano de diez dígitos usado para contacto.';
COMMENT
ON COLUMN usuario.password_hash IS 'Hash BCrypt de la contraseña; nunca almacena texto plano.';
COMMENT
ON COLUMN usuario.activo IS 'Indica si el usuario puede iniciar nuevas operaciones.';
COMMENT
ON COLUMN usuario.creado_en IS 'Fecha de creación de la identidad.';
COMMENT
ON COLUMN usuario.actualizado_en IS 'Fecha de última actualización de los datos de identidad.';

COMMENT
ON TABLE rol IS 'Catálogo de roles asignables a usuarios de RutaSur.';
COMMENT
ON COLUMN rol.id IS 'Identificador interno UUID del rol.';
COMMENT
ON COLUMN rol.nombre IS 'Nombre único del rol de autorización.';
COMMENT
ON COLUMN rol.descripcion IS 'Descripción funcional del rol.';
COMMENT
ON COLUMN rol.activo IS 'Indica si el rol puede asignarse.';
COMMENT
ON COLUMN rol.creado_en IS 'Fecha de creación del rol.';

COMMENT
ON TABLE usuario_rol IS 'Relación de roles asignados a cada usuario.';
COMMENT
ON COLUMN usuario_rol.usuario_id IS 'Usuario al que se asigna el rol.';
COMMENT
ON COLUMN usuario_rol.rol_id IS 'Rol asignado al usuario.';
COMMENT
ON COLUMN usuario_rol.asignado_en IS 'Fecha de asignación del rol.';
COMMENT
ON COLUMN usuario_rol.asignado_por_usuario_id IS 'Administrador que realizó la asignación cuando aplica.';

COMMENT
ON TABLE sesion_autenticacion IS 'Sesión de autenticación que agrupa access y refresh tokens.';
COMMENT
ON COLUMN sesion_autenticacion.id IS 'Identificador UUID de la sesión.';
COMMENT
ON COLUMN sesion_autenticacion.usuario_id IS 'Usuario propietario de la sesión.';
COMMENT
ON COLUMN sesion_autenticacion.estado IS 'Estado de la sesión: ACTIVA, INVALIDADA o EXPIRADA.';
COMMENT
ON COLUMN sesion_autenticacion.ip_origen IS 'Dirección IP desde la que se inició la sesión.';
COMMENT
ON COLUMN sesion_autenticacion.agente_usuario IS 'Agente de usuario informado por el cliente.';
COMMENT
ON COLUMN sesion_autenticacion.iniciada_en IS 'Fecha de inicio de sesión.';
COMMENT
ON COLUMN sesion_autenticacion.expira_en IS 'Fecha máxima de expiración de sesión.';
COMMENT
ON COLUMN sesion_autenticacion.invalidada_en IS 'Fecha de invalidación, si existe.';

COMMENT
ON TABLE refresh_token IS 'Tokens de refresco de uso único almacenados exclusivamente como hash.';
COMMENT
ON COLUMN refresh_token.id IS 'Identificador UUID del refresh token.';
COMMENT
ON COLUMN refresh_token.sesion_id IS 'Sesión a la que pertenece el refresh token.';
COMMENT
ON COLUMN refresh_token.token_hash IS 'Hash no reversible del refresh token.';
COMMENT
ON COLUMN refresh_token.usado IS 'Indica si el refresh token ya fue utilizado.';
COMMENT
ON COLUMN refresh_token.creado_en IS 'Fecha de emisión del refresh token.';
COMMENT
ON COLUMN refresh_token.expira_en IS 'Fecha de vencimiento del refresh token.';
COMMENT
ON COLUMN refresh_token.usado_en IS 'Fecha de uso del refresh token.';
COMMENT
ON COLUMN refresh_token.reemplazado_por_id IS 'Refresh token emitido en la rotación exitosa.';

COMMENT
ON TABLE access_token_revocado IS 'Identificadores de access tokens revocados antes de su vencimiento.';
COMMENT
ON COLUMN access_token_revocado.id IS 'Identificador UUID del registro de revocación.';
COMMENT
ON COLUMN access_token_revocado.sesion_id IS 'Sesión que emitió el access token revocado.';
COMMENT
ON COLUMN access_token_revocado.jti IS 'JWT ID único del access token revocado.';
COMMENT
ON COLUMN access_token_revocado.expira_en IS 'Fecha natural de vencimiento del token revocado.';
COMMENT
ON COLUMN access_token_revocado.revocado_en IS 'Fecha de revocación.';
COMMENT
ON COLUMN access_token_revocado.motivo IS 'Motivo funcional de revocación.';

COMMENT
ON TABLE evento_autenticacion IS 'Auditoría de eventos relevantes de autenticación y autorización.';
COMMENT
ON COLUMN evento_autenticacion.id IS 'Identificador UUID del evento de auditoría.';
COMMENT
ON COLUMN evento_autenticacion.usuario_id IS 'Usuario relacionado con el evento cuando aplica.';
COMMENT
ON COLUMN evento_autenticacion.tipo IS 'Tipo de evento de autenticación.';
COMMENT
ON COLUMN evento_autenticacion.detalle IS 'Datos adicionales no sensibles del evento en formato JSON.';
COMMENT
ON COLUMN evento_autenticacion.correlacion_id IS 'Identificador de trazabilidad distribuida.';
COMMENT
ON COLUMN evento_autenticacion.ocurrido_en IS 'Fecha y hora del evento auditado.';

COMMENT
ON TABLE outbox_evento IS 'Eventos de integración pendientes de publicación confiable.';
COMMENT
ON COLUMN outbox_evento.id IS 'Identificador único e idempotente del evento.';
COMMENT
ON COLUMN outbox_evento.tipo IS 'Nombre del hecho de negocio publicado.';
COMMENT
ON COLUMN outbox_evento.version IS 'Versión del contrato del evento.';
COMMENT
ON COLUMN outbox_evento.agregado_id IS 'Identificador del agregado de identidad que produjo el evento.';
COMMENT
ON COLUMN outbox_evento.correlacion_id IS 'Identificador de trazabilidad distribuida.';
COMMENT
ON COLUMN outbox_evento.datos IS 'Carga útil serializada del evento.';
COMMENT
ON COLUMN outbox_evento.ocurrido_en IS 'Fecha de creación del evento en la transacción local.';
COMMENT
ON COLUMN outbox_evento.publicado_en IS 'Fecha de publicación exitosa; nula mientras esté pendiente.';
COMMENT
ON COLUMN outbox_evento.intentos_publicacion IS 'Número de intentos de entrega al bus de eventos.';
COMMENT
ON COLUMN outbox_evento.ultimo_error IS 'Último error técnico de publicación, si existe.';

COMMENT
ON TABLE inbox_evento IS 'Eventos consumidos para garantizar procesamiento idempotente.';
COMMENT
ON COLUMN inbox_evento.id_evento IS 'Identificador único del evento recibido.';
COMMENT
ON COLUMN inbox_evento.tipo IS 'Tipo del evento recibido.';
COMMENT
ON COLUMN inbox_evento.origen IS 'Servicio productor del evento recibido.';
COMMENT
ON COLUMN inbox_evento.recibido_en IS 'Fecha de recepción del evento.';
COMMENT
ON COLUMN inbox_evento.procesado_en IS 'Fecha de procesamiento exitoso.';
COMMENT
ON COLUMN inbox_evento.estado IS 'Estado de procesamiento del evento recibido.';
