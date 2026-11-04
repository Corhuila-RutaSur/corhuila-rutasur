# ADR-002: Migrar RutaSur a microservicios con hexagonal interna, CQRS y eventos

**Estado:** Aceptado  
**Fecha:** 2026-10-29  
**Decisión relacionada:** [ADR-001 de la V1](../v1/adr/001-arquitectura-por-capas.md)

## Contexto y problema arquitectónico

La V1 de RutaSur es un monolito con arquitectura por capas y una base de datos única. Es adecuada como MVP y se conserva inmutable en la etiqueta `v1`, pero concentra en una sola unidad el catálogo público, las operaciones críticas de reserva, los pagos, la ejecución turística y las notificaciones.

El principal problema no es únicamente el crecimiento técnico: los flujos tienen perfiles operativos distintos. Catálogo recibe muchas consultas públicas y debe escalar horizontalmente sin que ese tráfico afecte la reserva. Reservas requiere una decisión inmediata y consistente sobre cupos, porque una sobreventa no es aceptable. Pagos depende de una respuesta o evento externo, mientras que las notificaciones pueden procesarse después sin bloquear la experiencia del usuario.

Una base y un despliegue únicos convierten estos ritmos diferentes en una misma unidad de escalamiento, liberación y fallo. Además, hacen que el código técnico de HTTP, JPA, seguridad y mensajería termine mezclado con reglas del dominio turístico.

## Fuerzas y criterios de decisión

| Fuerza | Decisión que exige |
| --- | --- |
| Muchas consultas simultáneas de experiencias | Separar Catálogo y sus proyecciones de lectura para escalar sin arrastrar Reservas. |
| Cero sobreventa | Mantener cupos, bloqueos y confirmación en una transacción local del servicio Reservas. |
| Pago y notificación no inmediatos | Propagar hechos de negocio por eventos, sin esperar a que todos los consumidores respondan. |
| Evolución independiente por dominio | Un despliegue, repositorio lógico, base de datos y migraciones por microservicio. |
| Pruebas y mantenimiento del negocio | Arquitectura hexagonal para aislar el dominio de frameworks e infraestructura. |
| Lecturas con necesidades distintas | CQRS selectivo: proyecciones solo cuando aporten composición, rendimiento o escala. |
| Entorno educativo local reproducible | SNS/SQS emulado con Floci, sin cambiar el diseño de integración de producción. |

## Decisión

RutaSur evolucionará, manteniendo la rama `main` y la etiqueta inmutable `v1`, hacia una arquitectura compuesta por:

- Microservicios de **Identidad**, **Catálogo**, **Reservas**, **Pagos**, **Operación**, **Notificaciones** y **Consultas**.
- Una base de datos independiente y migraciones Flyway propias por microservicio.
- Arquitectura hexagonal dentro de cada microservicio: puertos de entrada, casos de uso, dominio, puertos de salida y adaptadores de infraestructura.
- CQRS selectivo: los comandos mantienen consistencia fuerte; las consultas se atienden desde proyecciones optimizadas y eventualmente consistentes cuando el caso lo permite.
- Comunicación asíncrona por eventos mediante SNS/SQS emulados localmente con Floci.
- Patrón outbox para publicación confiable e inbox para consumo idempotente.
- Una función serverless para el microservicio de Notificaciones.

El [diagrama C4 de contenedores](../architecture/c4-contenedores-v2.md) es la vista de referencia para entender los componentes desplegables y sus comunicaciones.

## Cómo se aplican los patrones

### Microservicios: límites operativos y de propiedad

El sistema se divide por capacidades de negocio y no por capas técnicas globales. Catálogo publica y consulta experiencias; Reservas es dueño de cupos y reservas; Pagos decide el estado de un cobro; Operación prepara y ejecuta la experiencia; Identidad administra acceso; Notificaciones entrega mensajes; Consultas materializa vistas de lectura.

Esta separación permite asignar capacidad donde realmente se necesita. Por ejemplo, un aumento de visitantes navegando experiencias escala Catálogo y Consultas, sin multiplicar instancias que bloquean cupos o procesan pagos.

### Hexagonal interna: independencia de infraestructura

Cada microservicio organiza su código en adaptadores de entrada, casos de uso, dominio, puertos de salida y adaptadores de infraestructura. Un controlador REST, un consumidor SQS, JPA, PostgreSQL o una integración de pagos solo implementan o invocan puertos; no contienen reglas centrales del negocio.

Esto permite probar la creación de un bloqueo de cupo con dobles de repositorio y publicador de eventos, sin iniciar una base de datos, HTTP o Floci. También permite reemplazar un adaptador sin reescribir las entidades ni los casos de uso.

### CQRS selectivo: lectura optimizada sin sacrificar consistencia

CQRS no se aplica como una regla mecánica a cada clase. Los comandos actualizan exclusivamente el modelo de escritura del servicio dueño. Las consultas públicas, paneles o vistas que combinan información de varios dominios se atienden desde Catálogo o desde proyecciones del servicio Consultas, actualizadas por eventos.

Por ello, una pantalla puede mostrar disponibilidad con un retraso breve, pero la solicitud de bloqueo siempre revalida cupos dentro de Reservas. CQRS se usa para escalar y simplificar consultas, no para distribuir una decisión crítica de negocio.

### Eventos: desacoplamiento con entrega confiable

Un cambio local y su evento se guardan de forma atómica mediante outbox. Un proceso publicador los entrega a un tópico SNS; cada consumidor recibe una copia en su cola SQS. En local, Floci emula esas APIs AWS bajo el endpoint `http://localhost:4566`. El consumidor registra el identificador en inbox antes o durante el procesamiento para ser idempotente. Así, una entrega repetida no duplica pagos, reservas ni notificaciones.

No se implementa una transacción distribuida. Cuando una acción posterior falla, el servicio dueño conserva su estado y se aplican reintentos, compensaciones o liberación de cupos según el evento y el caso de uso.

### ACID local y Saga para procesos distribuidos

Cada comando que modifica datos de un servicio se ejecuta en una transacción local **ACID**. Atomicidad, consistencia, aislamiento y durabilidad pertenecen a la base del servicio dueño; no se simulan con eventos ni se extienden a otras bases.

Cuando una operación debe coordinar cambios confirmados en más de un microservicio, se utiliza una **Saga coreografiada por eventos**. Cada paso confirma su transacción local, publica un evento desde outbox y, si no puede completarse el objetivo global, el servicio responsable ejecuta una acción compensatoria en su propio dominio. La saga no reemplaza ACID: la usa como base en cada paso.

| Escenario | Pasos de la Saga | Compensación |
| --- | --- | --- |
| Reserva y pago | Reservas bloquea cupos → Pagos procesa → Reservas confirma con `PagoAprobado` → Notificaciones y Consultas reaccionan. | Pago rechazado, vencimiento o fallo definitivo liberan el bloqueo y los cupos en Reservas. |
| Cancelación con pago confirmado | Reservas marca solicitud de cancelación → Pagos procesa reembolso → Operación libera recursos → Notificaciones comunica. | Si el reembolso falla, la reserva no pasa a estado cancelado definitivo; queda en un estado compensable y se reintenta. |
| Publicación de catálogo hacia reservas | Catálogo publica experiencia/servicio → Reservas y Consultas actualizan snapshots. | No requiere compensación financiera; se reintenta hasta materializar la proyección idempotentemente. |

Una operación interna a un único servicio —por ejemplo, descontar cupos y crear un bloqueo— **no** usa Saga: se resuelve con una transacción ACID local.

## Límites de propiedad

| Servicio | Es dueño de | No puede hacer |
| --- | --- | --- |
| Catálogo | Experiencias, imágenes y servicios ofrecidos | Reservar cupos ni modificar reservas. |
| Reservas | Salidas, cupos, bloqueos temporales, reservas y asistencia | Cobrar ni consultar tablas de Pagos. |
| Pagos | Intentos, referencias y resultado del pago | Confirmar directamente una reserva. |
| Notificaciones | Entregas, reintentos y plantillas | Alterar estados de reserva o pago. |
| Operación | Guías, conductores, vehículos y ejecución real | Modificar cupos o pagos. |
| Identidad | Cuentas, roles, JWT y sesiones | Ser fuente de datos operativos de reservas. |
| Consultas | Proyecciones CQRS de lectura | Ejecutar comandos de negocio. |

No existirán llaves foráneas, transacciones ni consultas SQL entre las bases de datos de servicios distintos. Las referencias externas usarán identificadores y eventos versionados.

## Comunicación síncrona y asíncrona

| Mecanismo | Uso | Motivo |
| --- | --- | --- |
| HTTPS por API Gateway | Interacción inmediata del frontend: explorar, iniciar sesión, solicitar bloqueo y consultar resultado local. | Entrega una respuesta clara al usuario sin exponer topología interna. |
| Eventos SNS/SQS | Propagar hechos ya confirmados: publicación, pago aprobado, reserva confirmada o cancelada. | Evita acoplamiento temporal y permite varios consumidores independientes. |
| Proyecciones CQRS | Catálogo público, mis reservas y paneles compuestos. | Optimiza lectura y evita que una pantalla consulte bases de otros dominios. |

El Gateway no contiene reglas de negocio ni accede a bases de datos. Enruta peticiones al servicio dueño y centraliza preocupaciones de borde como autenticación, versionado, correlación y límites de acceso.

## Consistencia de cupos

El comando de bloqueo y confirmación de cupo se ejecutará exclusivamente en Reservas, con una transacción local ACID y bloqueo pesimista de `salida_experiencia` y `programacion_servicio`.

Las proyecciones CQRS pueden mostrar disponibilidad con retraso breve, pero nunca autorizan una reserva. El comando siempre vuelve a validar la disponibilidad en el modelo de escritura de Reservas.

El bloqueo tiene vencimiento. Si el pago se aprueba, `PagoAprobado` provoca la confirmación local de la reserva. Si se rechaza o vence, Reservas libera los cupos en su propia transacción y publica el hecho resultante. El flujo completo está documentado en el [diagrama de flujo](../architecture/flujo-creacion-reserva-v2.md) y el [diagrama de secuencia](../architecture/secuencia-creacion-reserva-v2.md).

## Eventos principales

| Productor | Evento | Consumidores principales |
| --- | --- | --- |
| Catálogo | `ExperienciaPublicada`, `ServicioActualizado` | Reservas, Consultas. |
| Reservas | `ReservaCreada`, `ReservaConfirmada`, `ReservaCancelada` | Notificaciones, Consultas, Operación. El pago se inicia por comando explícito del cliente. |
| Pagos | `PagoAprobado`, `PagoRechazado` | Reservas, Notificaciones, Consultas. |
| Operación | `ExperienciaFinalizada` | Reservas, Consultas. |

## Consecuencias

### Beneficios

- Catálogo puede escalar de forma independiente ante carga intensa de consultas.
- Reservas conserva consistencia fuerte y aislamiento para impedir sobreventa.
- Nuevos consumidores de eventos se agregan sin alterar el flujo principal de reserva.
- El dominio se prueba sin depender de HTTP, JPA, Floci o PostgreSQL.
- Los modelos de lectura se optimizan según cada pantalla Angular.

### Costos aceptados

- Más proyectos, migraciones y configuraciones de despliegue.
- Consistencia eventual en paneles, catálogo y notificaciones.
- Necesidad de contratos de eventos, idempotencia, trazabilidad distribuida y monitoreo.
- Duplicación controlada de datos mediante snapshots y proyecciones.

### Riesgos y mitigaciones

| Riesgo | Mitigación acordada |
| --- | --- |
| Un mismo evento llega más de una vez | Inbox, identificador único, consumidores idempotentes y pruebas de repetición. |
| Un evento no se publica después de confirmar una transacción | Outbox persistente y publicador reintentable. |
| Una proyección muestra información desactualizada | Indicador de actualización y revalidación obligatoria en el comando de Reservas. |
| Complejidad operativa de varios servicios | Contenedores, variables versionadas, health checks, trazabilidad y Floci para desarrollo. |
| Cambio incompatible de mensaje | Envelope versionado, contratos documentados y evolución compatible hacia atrás. |

## Límites explícitos de esta decisión

- No se adopta event sourcing: las bases relacionales de cada servicio continúan siendo la fuente de verdad de sus comandos.
- No se divide el frontend en microfrontends; Angular sigue siendo un cliente único del API Gateway.
- No se distribuye la transacción de cupos ni se intenta usar CQRS para decidir disponibilidad.
- No se usa Saga para sustituir una transacción ACID local; solo coordina procesos que cruzan límites de servicio.
- La migración inicial no obliga a migrar todos los servicios en un único despliegue; los límites y contratos se definen antes de extraer código.

## Estrategia de migración

1. Mantener V1 inmutable mediante el tag `v1`.
2. Definir modelos, contratos y eventos de V2 antes de mover código.
3. Extraer Catálogo y Reservas como primeros servicios.
4. Implementar bloqueo temporal, outbox e inbox en Reservas.
5. Extraer Pagos y conectarlo por los eventos `PagoAprobado` y `PagoRechazado`.
6. Implementar Notificaciones serverless.
7. Crear proyecciones CQRS y trasladar las consultas del frontend hacia el API Gateway.
8. Medir V1 y V2 con la misma funcionalidad y carga antes de presentar resultados.

## Criterios para considerar lista la migración de una capacidad

1. El spec funcional está aprobado y enlaza ADR, endpoints, eventos y datos involucrados.
2. Existe un servicio dueño con base y migraciones independientes.
3. La lógica de negocio está cubierta por pruebas unitarias y los adaptadores críticos por pruebas de integración o contrato.
4. Todo evento producido usa outbox y todo consumidor persistente usa inbox e idempotencia.
5. La trazabilidad permite seguir una operación mediante `correlacionId` entre Gateway, servicios y consumidores.
6. Los diagramas, OpenAPI, DBML, README y Wiki de consulta se actualizan si el cambio los afecta.

## Alternativas descartadas

| Alternativa | Motivo de descarte |
| --- | --- |
| Mantener únicamente arquitectura por capas | No permite despliegue ni escalamiento independiente por dominio. |
| Microservicios con base de datos compartida | Genera un monolito distribuido y rompe la autonomía de los servicios. |
| CQRS para todos los comandos | Pondría en riesgo la consistencia inmediata de cupos. |
| Comunicación síncrona entre todos los servicios | Aumenta acoplamiento temporal y propaga fallos hacia el flujo de reserva. |
