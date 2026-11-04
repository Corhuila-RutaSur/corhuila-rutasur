-- Fortalece la integridad de contacto para el flujo de usuarios.
-- El telefono pasa a ser obligatorio, unico y valido para telefonia movil colombiana.

alter table usuario
    alter column telefono type varchar(10),
    alter column telefono set not null;

alter table usuario
    add constraint uk_usuario_telefono unique (telefono);

alter table usuario
    add constraint ck_usuario_telefono_colombiano check (telefono ~ '^3[0-9]{9}$');

create unique index uk_usuario_correo_normalizado on usuario (lower(correo));

comment on column usuario.telefono is 'Telefono movil colombiano unico: inicia en 3 y tiene exactamente 10 digitos.';
