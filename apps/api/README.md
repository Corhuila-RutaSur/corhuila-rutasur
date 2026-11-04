# Backend RutaSur

Este directorio organiza proyectos backend hermanos; **no es un proyecto Maven agregador**. Cada microservicio se compila, prueba, configura y despliega por separado. La V1 se conserva temporalmente en `legacy-v1/` hasta que todas sus capacidades estén migradas y validadas.

## Módulos

| Módulo | Puerto local | Responsabilidad |
| --- | ---: | --- |
| `gateway` | 8080 | Borde HTTP de V2; enrutará al servicio dueño. |
| `identidad-service` | 8091 | Usuarios, roles, sesiones y tokens. |
| `catalogo-service` | 8092 | Experiencias, imágenes y servicios ofrecidos. |
| `reservas-service` | 8093 | Salidas, cupos, bloqueos y reservas. |
| `pagos-service` | 8094 | Pagos e intentos de cobro. |
| `operacion-service` | 8095 | Guías, vehículos y ejecución de salidas. |
| `notificaciones-service` | 8096 | Entrega y reintento de notificaciones. |
| `consultas-service` | 8097 | Proyecciones CQRS de lectura. |
| `contracts` | — | Envelope técnico de eventos; no comparte entidades de dominio. |
| `legacy-v1` | 8090 | Monolito histórico temporal; se eliminará únicamente al finalizar la migración total. |

## Infraestructura local

La guía única y vigente para preparar y ejecutar la V2 está en [Ejecución local V2](../../docs/operations/ejecucion-local-v2.md). Este documento conserva el mapa técnico de los módulos.

Floci se levanta con Docker Compose:

```powershell
docker compose up -d
```

PostgreSQL permanece instalado y ejecutándose localmente en tu equipo. Cada microservicio se conecta por JDBC a PostgreSQL local mediante sus variables `*_DB_URL`; no se crea ni se ejecuta un segundo PostgreSQL en Docker.

Antes de iniciar los microservicios, crea una vez las siete bases V2 en PostgreSQL local con [infra/postgres/crear-bases-v2-locales.sql](infra/postgres/crear-bases-v2-locales.sql), usando pgAdmin o `psql`. Flyway creará después las tablas de cada servicio dentro de su propia base.

Floci se ejecuta en `http://localhost:4566` y emula las API de SNS y SQS. Es el emulador AWS oficial de RutaSur para desarrollo local; LocalStack no se utiliza en esta configuración.

La consola visual se ejecuta en un contenedor separado y se abre en `http://localhost:4500`. Esta configuración permite inspeccionar recursos de Floci sin montar el socket Docker dentro del emulador; para SNS/SQS no necesitamos otorgar ese privilegio elevado.

## Ejecución

Cada proyecto es independiente y no existe un wrapper Maven común en `apps/api`. Instale primero `contracts` con `mvn clean install`; luego, desde el directorio de cada servicio, use:

```powershell
mvn spring-boot:run
```

### Primer flujo disponible: Identidad

La migración V2 crea una cuenta académica local para iniciar las pruebas: `admin@rutasur.local`, contraseña `RutaSurAdmin2026!`, rol `ADMINISTRADOR`. La base almacena únicamente su hash BCrypt 12. Esta credencial es pública y exclusiva del entorno local académico; debe sustituirse por secretos gestionados fuera de este contexto.

```powershell
cd identidad-service
mvn spring-boot:run
```

El servicio queda en `http://localhost:8091`. Los contratos operativos iniciales son `POST /api/v2/public/usuarios` y `POST /api/v2/public/autenticacion/login`. El primero valida los campos obligatorios, correo, teléfono colombiano y unicidad; crea únicamente el rol `CLIENTE`, cifra la contraseña y registra `UsuarioCreado` en Outbox. El segundo valida credenciales, crea una sesión local, firma el access token, genera un refresh token y persiste solamente su hash. Toda respuesta usa `codigoHttp`, `codigo`, `mensaje` y `data`; el mensaje es legible y localizado.

Los contratos protegidos de Identidad incluyen `GET /api/v2/usuarios/me`, `POST /api/v2/autenticacion/logout`, `GET /api/v2/administracion/roles` y `POST /api/v2/administracion/usuarios`. Las dos últimas rutas exigen el rol `ADMINISTRADOR`; los roles se consultan y validan contra el catálogo persistido `rol`.

### Corte inicial de Catálogo

Catálogo queda en `http://localhost:8092`. Su primer contrato extraído es
`POST /api/v2/administracion/experiencias`: crea una experiencia activa y valida
duración positiva. La autorización central de las rutas administrativas se conectará
desde `gateway`; hasta entonces el contrato solo se usa desde la red local de desarrollo.

En ejecución local el frontend puede apuntar directamente al servicio dueño mientras
se construye el Gateway. Floci no emula ni sustituye el Gateway: se reserva para
SNS/SQS y sus recursos de mensajería.

La migración funcional se hace por specs. El frontend no redirige rutas hacia V2 hasta que el flujo correspondiente esté extraído, probado y operativo.

## Reglas de consistencia

- Cada servicio confirma sus cambios con una transacción local ACID.
- Reservas es el único servicio que decide disponibilidad y bloquea cupos.
- Las operaciones distribuidas usan Saga coreografiada con eventos, outbox, inbox e idempotencia.
- Una Saga coordina transacciones ya confirmadas; no sustituye ACID ni crea una transacción entre bases.

Consulta el [ADR-002](../../docs/adr/002-migracion-a-microservicios-v2.md), el [C4](../../docs/architecture/c4-contenedores-v2.md) y el [catálogo de eventos](../../docs/events/README.md).
