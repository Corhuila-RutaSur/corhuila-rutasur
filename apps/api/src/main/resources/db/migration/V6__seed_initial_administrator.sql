-- Cuenta inicial de administración para entornos académicos y locales.
-- En un despliegue real debe cambiarse la contraseña o reemplazarse por un secreto externo.
-- La migración no duplica la cuenta ni su rol cuando se ejecuta más de una vez.

create extension if not exists pgcrypto;

insert into usuario (
    tipo_documento, numero_documento, nombres, apellidos, correo, telefono, password_hash, activo
)
select
    'CC', '1000000000', 'Administrador', 'RutaSur', 'admin@rutasur.local', '3000000000',
    crypt('RutaSurAdmin2026!', gen_salt('bf', 12)), true
where not exists (
    select 1 from usuario where lower(correo) = 'admin@rutasur.local'
);

insert into usuario_tipo (usuario_id, tipo_usuario_id)
select usuario.id, tipo_usuario.id
from usuario
join tipo_usuario on tipo_usuario.nombre = 'ADMINISTRADOR'
where lower(usuario.correo) = 'admin@rutasur.local'
on conflict (usuario_id, tipo_usuario_id) do nothing;
