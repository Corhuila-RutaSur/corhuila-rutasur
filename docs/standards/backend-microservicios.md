# Estándar backend: microservicios, hexágono y CQRS

Este estándar reemplaza `docs/v1/standards/backend-spring.md` para todo código V2. Lo histórico se conserva sin reescritura en `docs/v1/`.

## Estructura interna

```text
adapter/in/http/             # controlador e interfaz OpenAPI
adapter/out/persistence/     # JPA, entidad y mapper de persistencia
adapter/out/messaging/       # Outbox, publicador y consumidores
application/port/in/         # comandos, consultas y casos de uso
application/port/out/        # dependencias requeridas
application/usecase/         # orquestación
domain/model/                # agregado, valores e invariantes
domain/exception/            # errores de dominio
```

Los adaptadores dependen de puertos; el dominio no depende de Spring, JPA, HTTP, Floci ni otro microservicio.

## API y aplicación

- Todo controlador implementa contrato OpenAPI. Request, response y evento son `record`; HTTP usa `RespuestaGeneral<T>` y `@JsonInclude(NON_NULL)`.
- Validación de formato en request; reglas de negocio en agregado/caso de uso. Las entidades JPA no salen del adaptador de persistencia.
- MapStruct mapea bordes; usa `@RequiredArgsConstructor`. Consultas retornan DTO/proyección; modificaciones acotadas no cargan/guardan una entidad completa sin necesidad.
- Español sin `ñ`, nombres expresivos, Javadoc público, complejidad ciclomática máxima 8. Errores centralizados e internacionalizados por `message.properties`.

## Datos, concurrencia y eventos

- Flyway administra solo la base propia, previamente creada. Una migración aplicada es inmutable y documenta tablas/columnas con `COMMENT ON`.
- Cada invariante local se protege con transacción ACID. Reserva: capacidad, bloqueo y descuento de cupos son atómicos en `reservas-service`.
- El cambio y Outbox se escriben juntos. Consumidores usan Inbox/idempotencia. Todo evento tiene versión y `correlationId`.
- Saga solo para efectos distribuidos compensables. No hay transacciones entre bases y una Saga no reemplaza el bloqueo local.

## Seguridad, logs y pruebas

- Autorización real en backend; no se aceptan roles del cliente. No hay secretos versionados.
- Cada entrada, decisión, evento y salida escribe miga de pan estructurada: correlación, servicio, caso de uso, agregado, resultado y duración; nunca tokens, contraseñas ni datos personales completos.
- Pruebas de éxito, rechazo y borde; unitarias en dominio/casos de uso, integración en adaptadores y contrato para API/eventos.
