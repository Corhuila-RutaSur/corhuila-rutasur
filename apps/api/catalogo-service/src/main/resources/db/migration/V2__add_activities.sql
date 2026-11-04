-- Spec V2: docs/specs/003-catalogo-de-experiencias.md
CREATE TABLE actividad
(
    id             UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    experiencia_id UUID         NOT NULL REFERENCES experiencia (id),
    nombre         VARCHAR(150) NOT NULL,
    descripcion    TEXT         NOT NULL,
    duracion_horas INTEGER      NOT NULL,
    activa         BOOLEAN      NOT NULL DEFAULT TRUE,
    creado_en      TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMPTZ,
    CONSTRAINT ck_actividad_duracion_positiva CHECK (duracion_horas > 0)
);

CREATE INDEX ix_actividad_experiencia_activa ON actividad (experiencia_id, activa);

COMMENT
ON TABLE actividad IS 'Actividad turística propia de una experiencia, seleccionable directamente al reservar una salida.';
COMMENT
ON COLUMN actividad.experiencia_id IS 'Experiencia propietaria de la actividad.';
COMMENT
ON COLUMN actividad.duracion_horas IS 'Duración estimada de la actividad en horas.';
