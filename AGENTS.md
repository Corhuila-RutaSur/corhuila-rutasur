# RutaSur: guía de trabajo V2

## Alcance y fuente de verdad

- La arquitectura vigente es la V2: microservicios, arquitectura hexagonal interna, CQRS selectivo, eventos y propiedad exclusiva de datos por servicio. Su decisión está en `docs/adr/002-migracion-a-microservicios-v2.md`.
- `docs/v1/` es histórico, de solo consulta. No se modifica para introducir funcionalidades V2 ni se toma como arquitectura destino.
- Antes de cambiar código, lee la especificación V2 aplicable en `docs/specs/`, el ADR, la arquitectura y el estándar técnico correspondiente.
- Usa `docs/specs/trazabilidad-v1-v2.md` para identificar la regla V1 que se está trasladando. No se permite perder, reinterpretar ni duplicar una regla sin registrar el cambio en la especificación y en esa trazabilidad.
- Actualiza en el mismo cambio: especificación, ADR si la decisión es duradera, diagrama o modelo si cambian, README si afecta el uso y la página equivalente de la Wiki. Un cambio no está terminado hasta que estas evidencias estén alineadas.
- Todo cambio de flujo, contrato, persistencia, evento, responsabilidad de servicio o decisión técnica debe registrar qué cambió, motivo, impacto, pruebas y documentos actualizados.
- No inventes requisitos. Declara cualquier supuesto que modifique el comportamiento antes de implementarlo.

## Estructura y dueños

- `apps/web`: Angular; consume solamente el API Gateway y no servicios internos.
- `apps/api/gateway`: borde HTTP, autenticación de borde, enrutamiento y correlación.
- `apps/api/identidad-service`: usuarios, roles, credenciales, sesiones y tokens.
- `apps/api/catalogo-service`: experiencias, imágenes y servicios ofertables.
- `apps/api/reservas-service`: salidas, capacidad, reservas, participantes y bloqueo transaccional de cupos.
- `apps/api/pagos-service`: intención, confirmación, reversión y trazabilidad de pagos.
- `apps/api/operacion-service`: ejecución, asistencia, guía, conductor y vehículo.
- `apps/api/notificaciones-service`: entrega de comunicaciones; no es dueño de reglas de reservas.
- `apps/api/consultas-service`: proyecciones de lectura reconstruibles; nunca fuente de verdad de una operación.
- `apps/api/contracts`: contratos versionados de integración, sin entidades de dominio ni clientes de infraestructura.
- `docs/v1`: evidencia histórica. `docs/specs`: comportamiento V2. `docs/standards`: reglas obligatorias. `docs/architecture`: diagramas y límites. `docs/events`: contratos de eventos.

## Reglas de implementación V2

- Cada microservicio tiene base de datos y migraciones Flyway propias. Está prohibido leer, escribir o crear claves foráneas hacia la base de otro servicio.
- Dentro de cada servicio usa dependencias hacia el interior: `adapter/in` -> `application` -> `domain`; los adaptadores de salida implementan puertos definidos por el dominio o aplicación. Ningún controlador conoce JPA ni una entidad de persistencia cruza el límite HTTP.
- Los comandos modifican el modelo transaccional del servicio dueño; las consultas usan modelos de lectura o proyecciones sin efectos secundarios. No se aplica CQRS ceremonial donde no aporte separación real.
- Usa transacciones ACID para invariantes locales. Para un flujo que alcance más de un servicio, publica eventos mediante Outbox, consume de forma idempotente mediante Inbox y diseña compensaciones Saga cuando corresponda. Nunca reemplaces el bloqueo local de cupos por una Saga.
- Todo evento incluye identificador, tipo, versión, origen, fecha y correlación. Los consumidores toleran reintentos y duplicados.
- Conserva el contrato HTTP uniforme `RespuestaGeneral<T>`; usa `@JsonInclude(NON_NULL)`, errores internacionalizados desde `message.properties` y códigos estables. Los DTO de entrada y salida son `record`.
- Controladores implementan una interfaz OpenAPI; las reglas viven en casos de uso. Usa MapStruct en adaptadores de mapeo, `@RequiredArgsConstructor`, validación Bean Validation, nombres en español sin `ñ`, Javadoc y complejidad ciclomática máxima de 8.
- No hay secretos en código ni en archivos versionados. Configura puertos, bases, JWT y Floci por variables de entorno.
- Cada solicitud y transición relevante produce trazabilidad estructurada con `correlationId`, servicio, caso de uso, evento y resultado; no se registran contraseñas, JWT ni datos personales innecesarios.

## Calidad, pruebas y documentación

- Toda regla nueva o migrada exige pruebas de caso exitoso, rechazo esperado y límite relevante. Mantén JUnit, ArchUnit y pruebas de integración/contrato proporcionales.
- ArchUnit debe proteger los límites hexagonales y la independencia entre módulos. Los contratos de evento y de API se prueban entre productor y consumidor.
- No se desactivan pruebas, validaciones, controles de seguridad ni reglas de calidad para obtener una compilación verde.
- Los criterios de aceptación son observables. Cada especificación declara alcance, fuera de alcance, dueño, contrato, datos, eventos, reglas conservadas de V1 y pruebas.
- El repositorio Wiki se actualiza en el mismo cambio documental. En Wiki, V1 se identifica mediante la sección **V1 · Histórico** porque GitHub Wiki no admite carpetas mediante títulos con `/`.
- No añadas dependencias, despliegues, commits, publicaciones ni cambios externos sin autorización explícita.
