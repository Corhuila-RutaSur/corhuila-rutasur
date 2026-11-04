# 002 — Identidad, autorización y sesiones

**Estado:** Borrador  
**Servicio dueño:** Identidad  
**Origen V1:** Specs 008, 011 y 013; módulos de usuarios y autenticación.  
**Decisiones relacionadas:** [ADR-002](../adr/002-migracion-a-microservicios-v2.md), [modelo de datos V2](../architecture/modelo-datos-v2.md), [trazabilidad V1 → V2](trazabilidad-v1-v2.md).

## Objetivo y valor

Extraer usuarios, roles, credenciales, sesiones y tokens desde V1 hacia un servicio autónomo. Los demás dominios autorizarán y referenciarán identidades sin consultar sus tablas.

## Alcance

- Registro público de clientes con rol inicial `CLIENTE`.
- Administración autorizada de usuarios, estado y roles.
- Login, logout, emisión de access token y rotación de refresh token de uso único.
- Validación de token, revocación, auditoría y eventos de identidad.
- Persistencia exclusiva en `rutasur_identidad_db`, con Flyway, Outbox e Inbox propios.

## Fuera de alcance

- Inicio con terceros, MFA, recuperación de contraseña y proveedores externos.
- Pantallas Angular V2 y distribución definitiva de claves a Gateway.
- Compartir tablas, entidades o claves foráneas con cualquier otro microservicio.

## Actores y autorización

| Actor o servicio | Responsabilidad |
| --- | --- |
| Visitante | Registrarse e iniciar sesión. |
| Cliente autenticado | Consultar/actualizar su perfil y cerrar su sesión. |
| Administrador | Crear, actualizar, inactivar usuarios y asignar roles permitidos. |
| Gateway | Validar el access token antes de enrutar recursos protegidos. |
| Servicios consumidores | Usar identificador externo, roles/claims o eventos; jamás tablas de Identidad. |

## Reglas de negocio

1. Documento, correo y teléfono son únicos incluso si el usuario fue inactivado.
2. El teléfono colombiano comienza en `3` y tiene diez dígitos.
3. Registro público asigna exclusivamente `CLIENTE`.
4. Contraseñas usan BCrypt con factor 12 y nunca se publican ni registran.
5. Refresh token es de único uso; reutilizarlo invalida la sesión completa.
6. Token vencido se diferencia de token inválido o revocado para que el cliente renueve solo cuando corresponda.
7. La eliminación es lógica: inactiva al usuario, conserva evidencia e invalida sesiones.
8. Todo registro, rotación e inactivación se confirma mediante transacción ACID local y deja miga de pan con `correlationId`.

## Flujo principal

1. El visitante se registra por Gateway.
2. Identidad valida formato y unicidad, cifra contraseña, asigna `CLIENTE`, escribe Outbox y confirma una transacción local.
3. Publica `UsuarioCreado`.
4. Login valida credenciales, crea sesión, genera tokens y registra auditoría local.
5. Al vencer el access token, el cliente presenta refresh; Identidad lo rota y entrega un nuevo par.

## Casos alternativos y errores

- Duplicado de documento/correo/teléfono: regla específica sin filtrar datos.
- Credenciales incorrectas: respuesta genérica.
- Access vencido: `autenticacion.token.expirado`; inválido/revocado: `autenticacion.token.invalido`.
- Refresh vencido, usado o revocado: invalida sesión y requiere login.
- Inactivación administrativa: revoca sesiones y publica `UsuarioInactivado`.

## Contratos afectados

### HTTP

| Método y ruta V2 | Resultado |
| --- | --- |
| `POST /api/v2/public/usuarios` | Registra un cliente. |
| `POST /api/v2/public/autenticacion/login` | Crea sesión y tokens. |
| `POST /api/v2/public/autenticacion/refresh` | Rota refresh de un uso. |
| `POST /api/v2/autenticacion/logout` | Invalida la sesión actual. |
| `GET /api/v2/usuarios/me` | Devuelve perfil autenticado. |
| `GET /api/v2/administracion/roles` | Lista roles activos disponibles para asignación. |
| `POST/PATCH/DELETE /api/v2/administracion/usuarios` | Administración autorizada e inactivación lógica. |

### Eventos

| Dirección | Evento | Acción |
| --- | --- | --- |
| Publica | `UsuarioCreado` | Permite referencias externas. |
| Publica | `UsuarioInactivado` | Consumidores bloquean operaciones futuras. |
| Publica | `RolActualizado` | Actualiza autorizaciones/proyecciones dependientes. |

### Datos y migraciones

- Tablas: usuario, rol, usuario_rol, sesion_autenticacion, refresh_token, access_token_revocado, evento_autenticacion, outbox_evento e inbox_evento.
- Los demás servicios guardan `usuario_id_externo`; no usan FK hacia Identidad.
- La primera migración del servicio corresponde a este spec y documenta cada objeto con `COMMENT ON`.
- La migración V2 crea una sola cuenta administrativa académica local: `admin@rutasur.local`, contraseña pública `RutaSurAdmin2026!` y rol `ADMINISTRADOR`. La migración conserva exclusivamente su hash BCrypt 12. Esta excepción existe para permitir un arranque reproducible en el proyecto académico y no se admite en ambientes compartidos o productivos.

## Trazabilidad de migración

- **Reglas preservadas:** unicidad, teléfono colombiano, BCrypt 12, CLIENTE por registro público, tokens y borrado lógico.
- **Tipo:** comandos de identidad y consultas de perfil; no es una proyección CQRS compartida.
- **Consistencia:** ACID local; integración saliente mediante Outbox e idempotencia en consumidores.
- **Eventos/correlación:** `UsuarioCreado`, `UsuarioInactivado` y `RolActualizado` con `correlationId`.

## Criterios de aceptación

```gherkin
Escenario: Registro público crea un cliente
Dado que no existen el documento, correo ni teléfono recibidos
Cuando el visitante registra una cuenta
Entonces Identidad crea el usuario con rol CLIENTE
Y almacena la contraseña con BCrypt 12
Y publica UsuarioCreado mediante outbox.

Escenario: Reutilización de refresh token
Dado que un refresh token ya fue usado para renovar una sesión
Cuando el cliente intenta utilizarlo de nuevo
Entonces Identidad invalida la sesión asociada
Y responde autenticacion.refresh.invalido.

Escenario: Inactivación administrativa
Dado que un administrador autorizado selecciona un usuario activo
Cuando solicita la eliminación lógica
Entonces Identidad marca el usuario como inactivo
Y invalida sus sesiones activas
Y publica UsuarioInactivado.
```

## Implementación inicial

El primer corte vertical ya expone `POST /api/v2/public/usuarios` desde `identidad-service`. El contrato recibe una petición validada y responde `RespuestaGeneral<UsuarioRegistroResponse>` con `codigoHttp`, `codigo`, `mensaje` y `data`. La clave interna de error permanece como código estable; el cliente recibe siempre un `mensaje` localizado, nunca la clave como explicación.

El caso de uso:

- normaliza documento y correo;
- verifica documento, correo y teléfono únicos;
- cifra la contraseña mediante el puerto BCrypt con factor 12;
- asigna exclusivamente `CLIENTE` mediante una operación explícita de persistencia;
- registra `UsuarioCreado` en Outbox con `correlationId`;
- deja trazabilidad de inicio y éxito sin registrar contraseña ni datos personales en logs.

Las pruebas automatizadas cubren el registro correcto, el rechazo temprano de documento duplicado y una regla ArchUnit que impide dependencias desde `domain` hacia adapters, Spring o JPA.

El segundo corte vertical expone `POST /api/v2/public/autenticacion/login`. Tras validar las credenciales contra BCrypt y comprobar que la identidad esté activa, crea una sesión en `sesion_autenticacion`, firma un access token JWT con usuario, sesión y roles, crea un refresh token aleatorio y persiste solamente su SHA-256 en `refresh_token`. Los intentos exitosos y fallidos generan auditoría sin almacenar contraseñas, tokens ni datos personales en logs. La clave de firma proviene exclusivamente de `IDENTIDAD_JWT_SECRET`, con mínimo 32 caracteres.

Las pruebas automatizadas cubren emisión correcta de sesión/tokens, almacenamiento hasheado de refresh, rechazo de credenciales inexistentes, registro público y la regla ArchUnit del dominio.

El tercer corte vertical expone `POST /api/v2/public/autenticacion/refresh`. La búsqueda del hash del refresh usa bloqueo de fila (`SELECT ... FOR UPDATE`) dentro de la transacción ACID local. Una rotación exitosa marca el token anterior como usado, lo enlaza al nuevo token y conserva el vencimiento máximo original de la sesión. Un refresh reutilizado invalida de inmediato la sesión completa y registra `REFRESH_REUTILIZADO`; un refresh vencido, inexistente o asociado a una sesión/usuario no vigente responde `autenticacion.refresh.invalido` sin emitir nuevos tokens.

El cuarto corte vertical expone `POST /api/v2/autenticacion/logout`. El access token debe llegar como `Authorization: Bearer`; Identidad verifica firma, vencimiento, sesión activa y revocación previa. Luego registra su `jti` en `access_token_revocado`, invalida la sesión y audita `LOGOUT`. El filtro JWT protege todas las rutas que no sean `/api/v2/public/**` o salud: un token vencido responde `autenticacion.token.expirado` para que el cliente intente refresh; un token malformado, revocado o de sesión inactiva responde `autenticacion.token.invalido`. Ambos casos usan HTTP 401 y `RespuestaGeneral`.

El quinto corte vertical expone `GET /api/v2/usuarios/me`, `GET /api/v2/administracion/roles` y `POST /api/v2/administracion/usuarios`. El perfil propio utiliza una proyección SQL segura que excluye hashes, sesiones y tokens. Las rutas administrativas exigen `ADMINISTRADOR`. El catálogo de roles se consulta desde la tabla `rol`: el enum de Java solo expresa valores reconocidos por el dominio, pero no sustituye la fuente de verdad persistente. La creación administrativa exige uno o más roles existentes y activos, conserva las validaciones de unicidad, cifra la contraseña con BCrypt 12 y crea las relaciones `usuario_rol` en la misma transacción ACID. El registro público continúa asignando exclusivamente `CLIENTE` e ignora cualquier intento de selección de roles.

El sexto corte vertical completa el CRUD administrativo con `GET /api/v2/administracion/usuarios`, `GET /api/v2/administracion/usuarios/{id}`, `PATCH /api/v2/administracion/usuarios/{id}` y `DELETE /api/v2/administracion/usuarios/{id}`. Consulta por proyecciones SQL seguras; actualiza datos permitidos y reemplaza roles activos dentro de una transacción; la eliminación es lógica, marca `usuario.activo=false` e invalida todas las sesiones activas. No se borran identidades ni evidencia. Las pruebas cubren consulta, actualización con reemplazo de roles, duplicados e inactivación con revocación de sesiones.

## Pruebas requeridas

- Dominio: unicidad, roles, rotación e inactivación.
- Aplicación: registro, login, refresh y administración.
- Adaptadores: JPA/Flyway, BCrypt, JWT, Outbox e integración de seguridad.
- Arquitectura: el dominio no depende de Spring, JPA, HTTP ni Floci.
