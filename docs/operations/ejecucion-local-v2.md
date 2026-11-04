# Ejecucion local de RutaSur V2

Esta guia permite levantar RutaSur V2 desde un clon nuevo del repositorio. La V2 esta compuesta por microservicios independientes, PostgreSQL local y Floci para emular SNS/SQS. La V1 permanece como referencia historica y no hace parte de este arranque.

## 1. Arquitectura local

| Componente | Donde se ejecuta | Proposito |
| --- | --- | --- |
| PostgreSQL | Instalacion local | Una base independiente por microservicio. |
| Floci | Docker Desktop | Emulacion local de topicos SNS y colas SQS. |
| Microservicios Spring Boot | Equipo local | Logica de dominio, Flyway y APIs HTTP. |
| Angular | Equipo local | Cliente web; durante la migracion consume el borde disponible o el servicio duenio segun el flujo extraido. |

Floci **no** ejecuta PostgreSQL. Las bases se mantienen en el PostgreSQL local configurado en los `application.yaml` de cada servicio.

## 2. Prerrequisitos

- Git.
- Java 25 y Maven disponibles en la terminal (`java --version` y `mvn --version`).
- PostgreSQL local en el puerto configurado; por defecto `5433`.
- Docker Desktop iniciado y en estado operativo.
- Node.js y npm compatibles con Angular 22, solo si se levantara el frontend.

No se requiere instalar AWS CLI: el script de Floci usa temporalmente la imagen Docker `amazon/aws-cli:2.39.3`.

## 3. Crear las bases de datos V2

Las bases no las crea Flyway. Flyway versiona tablas, datos semilla y cambios **dentro** de una base ya existente. Ejecute una sola vez el script [`infra/postgres/crear-bases-v2-locales.sql`](../../infra/postgres/crear-bases-v2-locales.sql) desde pgAdmin o `psql` conectado al servidor local.

```powershell
psql -h localhost -p 5433 -U postgres -f .\infra\postgres\crear-bases-v2-locales.sql
```

El script crea estas siete bases:

```text
rutasur_identidad_db
rutasur_catalogo_db
rutasur_reservas_db
rutasur_pagos_db
rutasur_operacion_db
rutasur_notificaciones_db
rutasur_consultas_db
```

Si alguna ya existe, no la elimine para repetir el procedimiento. Continue con las restantes o valide su existencia en PostgreSQL.

## 4. Levantar Floci y crear recursos de mensajeria

Desde la raiz del repositorio:

```bash
cd infra/docker/floci
docker compose up -d
bash inicializar-recursos.sh
```

Verifique los contenedores y abra la consola visual:

```bash
docker compose ps
```

- Floci: `http://localhost:4566`
- Consola Floci: `http://localhost:4500`

`inicializar-recursos.sh` crea topicos SNS, colas SQS por consumidor y suscripciones. Puede ejecutarse de nuevo: valida la suscripcion existente antes de crearla, por lo que no debe duplicar consumidores.

| Topico | Colas suscritas |
| --- | --- |
| `catalogo.experiencia` | `reservas-catalogo`, `consultas-catalogo` |
| `reservas.cupo` | `pagos-cupo`, `consultas-cupo` |
| `reservas.reserva` | `consultas-reserva`, `notificaciones-reserva`, `operacion-reserva`, `pagos-reserva` |
| `pagos.pago` | `reservas-pago`, `notificaciones-pago`, `consultas-pago` |
| `operacion.salida` | `reservas-operacion`, `consultas-operacion` |

No borre el volumen `rutasur-floci-data` salvo que quiera reiniciar intencionalmente la mensajeria local. Si se elimina o se recrea Floci sin ese volumen, ejecute nuevamente el script de inicializacion.

## 5. Configuracion local obligatoria

Los valores por defecto de bases asumen PostgreSQL en `localhost:5433`, usuario `postgres` y clave `postgres`. Si su instalacion usa otros datos, defina las variables del servicio antes de iniciarlo; por ejemplo:

```powershell
$env:IDENTIDAD_DB_URL = 'jdbc:postgresql://localhost:5432/rutasur_identidad_db'
$env:IDENTIDAD_DB_USERNAME = 'postgres'
$env:IDENTIDAD_DB_PASSWORD = 'tu_contrasena'
```

La primera ejecucion de **Identidad** no requiere una contraseña por variable. La migracion V2 crea una cuenta administrativa academica local, con los siguientes datos:

```text
Correo:      admin@rutasur.local
Contraseña:  RutaSurAdmin2026!
Rol:         ADMINISTRADOR
```

La migracion almacena solamente un hash BCrypt con factor 12; la contraseña se publica deliberadamente porque este es un entorno academico local. No debe trasladarse esta credencial a ningun ambiente compartido, de pruebas institucionales o produccion. `IDENTIDAD_JWT_SECRET` conserva un valor local por defecto y puede reemplazarse por variable si se requiere.

## 6. Compilar el contrato compartido

Cada microservicio usa el artefacto local `rutasur-contracts`. Antes de compilar o iniciar por primera vez los servicios, instale ese artefacto en el repositorio Maven local:

```powershell
cd apps\api\contracts
mvn clean install
```

No existe un Maven multi-modulo ni un `mvnw` comun en `apps/api`; los microservicios se ejecutan de forma independiente con Maven instalado.

## 7. Orden de inicio recomendado

Abra una terminal por microservicio. Antes de iniciar un servicio que dependa de mensajeria, confirme que el paso 4 termino correctamente.

| Orden | Directorio | Comando | Puerto |
| ---: | --- | --- | ---: |
| 1 | `apps/api/identidad-service` | `mvn spring-boot:run` | 8091 |
| 2 | `apps/api/catalogo-service` | `mvn spring-boot:run` | 8092 |
| 3 | `apps/api/reservas-service` | `mvn spring-boot:run` | 8093 |
| 4 | `apps/api/pagos-service` | `mvn spring-boot:run` | 8094 |
| 5 | `apps/api/operacion-service` | `mvn spring-boot:run` | 8095 |
| 6 | `apps/api/notificaciones-service` | `mvn spring-boot:run` | 8096 |
| 7 | `apps/api/consultas-service` | `mvn spring-boot:run` | 8097 |
| 8 | `apps/api/gateway` | `mvn spring-boot:run` | 8080 |

Ejemplo para Catalogo:

```powershell
cd apps\api\catalogo-service
mvn spring-boot:run
```

Flyway se ejecuta al iniciar cada servicio y crea exclusivamente sus tablas dentro de su propia base. `ddl-auto: validate` verifica que el mapeo sea compatible; no crea ni altera tablas.

## 8. Verificacion

Cada microservicio expone salud en `http://localhost:<puerto>/actuator/health`. Ejemplos:

```text
http://localhost:8091/actuator/health  Identidad
http://localhost:8092/actuator/health  Catalogo
http://localhost:8093/actuator/health  Reservas
http://localhost:8097/actuator/health  Consultas
http://localhost:8080/actuator/health  Gateway
```

Todos los microservicios V2 exponen el mismo conjunto de documentación local:

```text
http://localhost:<puerto>/scalar                 Interfaz Scalar
http://localhost:<puerto>/swagger-ui/index.html  Interfaz Swagger UI
http://localhost:<puerto>/v3/api-docs            Contrato OpenAPI JSON
```

Por ejemplo, la documentación de Identidad está en `http://localhost:8091/scalar` y la de Catálogo en `http://localhost:8092/scalar`. La ruta redirige a una página HTML estática incluida en cada microservicio y esta carga Scalar contra su propio `/v3/api-docs`; así no depende de una ruta implícita del starter. En Identidad esas rutas son públicas; no requieren JWT ni muestran el formulario automático de Spring Security.

Después de actualizar el proyecto, detenga e inicie nuevamente cada microservicio. Agregar dependencias Maven no se aplica a un proceso que ya estaba ejecutándose.

Durante la migración local, Catálogo se consume de forma directa para pruebas y no incorpora una seguridad HTTP propia incompleta. El formulario `Please sign in` de Spring Security fue eliminado porque no era autenticación RutaSur ni validaba roles. La autorización de borde corresponde al Gateway y debe completarse antes de exponer estas rutas fuera del entorno local académico.

Cuando Catalogo y Consultas esten levantados, pruebe la lectura publica V2:

```text
GET http://localhost:8097/api/v2/public/experiencias
```

Revise la consola Floci para confirmar que topicos, colas y mensajes existen. Un fallo por una cola inexistente se corrige iniciando Floci y ejecutando una vez `bash inicializar-recursos.sh`; no modificando las URLs de cola dentro del codigo.

## 9. Frontend

En una terminal adicional:

```powershell
cd apps\web
npm install
npm start
```

El frontend se sirve normalmente en `http://localhost:4200`.

Mientras no exista Gateway local, Angular apunta directamente a los microservicios desde `apps/web/src/app/core/config/api.config.ts`: Identidad `8091`, Catálogo `8092`, Reservas `8093`, Pagos `8094` y Consultas `8097`. Esta topología es exclusivamente local; el despliegue objetivo conserva API Gateway como único borde.

Los microservicios HTTP permiten explícitamente los orígenes locales `http://localhost:4200` y `http://localhost:4201`, con los métodos y encabezados usados por Angular. Tras actualizar esta configuración, reinicie los microservicios que estuvieran encendidos; CORS se registra al iniciar Spring Boot.

## Datos de prueba

Después de iniciar los servicios, utilice el [cargador de datos V2](carga-datos-prueba-v2.md). El script crea un escenario coherente de catálogo, salida, cliente, reserva y pago sin reutilizar UUID manualmente.

## 10. Problemas frecuentes

| Sintoma | Causa probable | Accion |
| --- | --- | --- |
| La migracion V2 habia fallado antes de esta correccion | Flyway no alcanzo a registrar la semilla del administrador. | Actualice el proyecto y reinicie Identidad; PostgreSQL reintentara la migracion transaccional. No use Flyway clean. |
| `ObjectMapper ... could not be found` | Clases compiladas antes de la configuracion actual o dependencia sin recompilar. | Ejecute `mvn clean test` en el microservicio y vuelva a iniciarlo. |
| Error por cola/topico inexistente | Floci no esta arriba o sus recursos no fueron inicializados. | `cd infra/docker/floci`, `docker compose up -d`, luego `bash inicializar-recursos.sh`. |
| No se resuelve `rutasur-contracts` | Aun no se instalo el contrato en Maven local. | En `apps/api/contracts`, ejecute `mvn clean install`. |
| Conexion rechazada a PostgreSQL | Puerto, usuario, clave o base no coinciden. | Compruebe PostgreSQL y defina las variables `*_DB_URL`, `*_DB_USERNAME`, `*_DB_PASSWORD`. |
| Una imagen Docker de AWS CLI no descarga | Docker Desktop no tiene acceso a Docker Hub. | Verifique conectividad y vuelva a ejecutar el script; no es necesario instalar AWS CLI en Windows. |

## 11. Reglas de operacion local

- No almacenar claves, tokens ni contrasenias reales en `application.yaml`, commits o capturas.
- No modificar una migracion Flyway que ya se haya aplicado. Crear una nueva migracion para cada cambio posterior.
- No eliminar bases o volumenes para resolver un error sin antes identificar el servicio afectado: esos recursos contienen el estado local de las pruebas.
- Las decisiones de despliegue productivo estan en el ADR-004. Floci y PostgreSQL local existen solo para el entorno de desarrollo academico.
