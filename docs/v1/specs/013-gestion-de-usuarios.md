# 013 — Gestión de usuarios

## Objetivo

Permitir el registro y la administración lógica de cuentas de usuario sin exponer contraseñas ni eliminar su trazabilidad histórica.

## Alcance

- Registro público de una cuenta de usuario.
- Consulta individual y listado de cuentas activas.
- Actualización de los datos personales obligatorios.
- Inactivación lógica de una cuenta; no se ejecutan eliminaciones físicas.
- Validación de unicidad para documento, correo y teléfono.

## Reglas de negocio

1. Tipo y número de documento, nombres, apellidos, correo, teléfono y contraseña son obligatorios al registrar una cuenta.
2. No pueden existir dos usuarios con el mismo número de documento, correo o teléfono.
3. El correo se normaliza a minúsculas antes de validar su unicidad.
4. El teléfono debe iniciar en `3` y contener exactamente diez dígitos; corresponde a un número móvil colombiano.
5. La contraseña se almacena únicamente como hash BCrypt con factor de trabajo 12. Nunca se retorna, registra ni persiste en texto plano.
6. La eliminación de una cuenta es lógica: se actualiza `usuario.activo` a `false` y se conserva todo su historial.
7. Una cuenta inactiva no aparece en consultas ordinarias ni se puede actualizar mediante este flujo.
8. El registro es público. Las operaciones de consulta, actualización e inactivación requieren autenticación cuando se implemente JWT.
9. Toda cuenta creada recibe automáticamente el rol `CLIENTE` activo, definido en el catálogo inicial de roles.

## Contratos API

| Operación | Ruta | Acceso | Resultado |
| --- | --- | --- | --- |
| Registrar | `POST /api/v1/public/usuarios` | Público | `201 Created` y usuario sin credenciales. |
| Consultar | `GET /api/v1/usuarios/{id}` | Protegido | Usuario activo o `404`. |
| Listar | `GET /api/v1/usuarios` | Protegido | Solo usuarios activos. |
| Actualizar | `PUT /api/v1/usuarios/{id}` | Protegido | Datos actualizados o error de unicidad. |
| Inactivar | `DELETE /api/v1/usuarios/{id}` | Protegido | `204 No Content`; no borra filas. |

## Criterios de aceptación

```gherkin
Escenario: Registro válido
Dado que una persona envía todos los datos obligatorios
Y su documento, correo y teléfono no pertenecen a otro usuario
Cuando registra su cuenta
Entonces el sistema crea un usuario activo
Y persiste un hash BCrypt de la contraseña
Y responde sin contraseña ni hash.

Escenario: Teléfono colombiano inválido
Dado que una persona registra un teléfono que no inicia en 3 o no tiene diez dígitos
Cuando envía el registro
Entonces el sistema responde 400
Y reporta el error asociado al campo teléfono.

Escenario: Eliminación lógica
Dado que existe un usuario activo
Cuando se solicita su eliminación autorizada
Entonces el sistema marca la cuenta como inactiva
Y conserva la fila y sus relaciones históricas.
```

## Fuera de alcance

- Inicio de sesión, emisión de JWT, refresh tokens y recuperación de contraseña.
- Cambio de contraseña desde el perfil.
- Gestión administrativa de roles operativos.
