-- Spec V2: docs/specs/002-identidad-autorizacion-y-sesiones.md
-- Crea la cuenta administrativa local una única vez.
-- Cuenta académica local para permitir el arranque reproducible desde un clon nuevo.
-- El hash BCrypt usa factor 12 y corresponde a la contraseña documentada en
-- docs/operations/ejecucion-local-v2.md. No debe reutilizarse fuera del entorno académico.

INSERT INTO usuario (tipo_documento,
                     numero_documento,
                     nombres,
                     apellidos,
                     correo,
                     telefono,
                     password_hash,
                     activo)
SELECT 'CC',
       '${identidad_admin_documento}',
       'Administrador',
       'RutaSur',
       lower('${identidad_admin_correo}'),
       '${identidad_admin_telefono}',
       '$2a$12$qZVt8lwSJwB7j2HWxMClze5OtZXxPBUI3IOGwBo.m1mfk2s38Qkra',
       TRUE WHERE NOT EXISTS (
    SELECT 1
    FROM usuario
    WHERE lower(correo) = lower('${identidad_admin_correo}')
);

INSERT INTO usuario_rol (usuario_id, rol_id)
SELECT usuario.id, rol.id
FROM usuario
         JOIN rol ON rol.nombre = 'ADMINISTRADOR'
WHERE lower(usuario.correo) = lower('${identidad_admin_correo}') ON CONFLICT (usuario_id, rol_id) DO NOTHING;
