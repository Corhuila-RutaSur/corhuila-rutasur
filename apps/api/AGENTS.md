# Reglas para `apps/api` — V2

Lee primero `../../AGENTS.md`, la especificación V2 aplicable y el ADR 002. Este directorio contiene la migración progresiva desde `legacy-v1`; esa carpeta es referencia histórica y no destino de nuevos cambios.

## Límites obligatorios

- Cada servicio es desplegable y dueño de su propia base de datos, migraciones Flyway, modelo de dominio y Outbox/Inbox cuando publique o consuma eventos.
- No se comparte una entidad, repositorio JPA, tabla, migración ni esquema entre microservicios. `contracts` solo contiene contratos versionados de integración.
- La organización mínima es `adapter/in`, `application/port/in`, `application/usecase`, `domain/model`, `domain/port/out` y `adapter/out`.
- Las dependencias apuntan hacia el dominio. Un adaptador HTTP no invoca JPA; una entidad de persistencia no viaja por HTTP ni por eventos.
- Comandos y consultas se separan cuando su carga, modelo o consistencia lo justifique. Una proyección de consulta nunca valida ni muta reglas de negocio.

## Consistencia y seguridad

- Una transacción ACID cubre únicamente cambios de un mismo servicio. Para interacción distribuida usa Outbox + evento + consumidor idempotente; usa Saga con compensación explícita cuando el flujo lo requiera.
- La reserva de cupos es una invariante local de `reservas-service`: debe conservar bloqueo concurrente y validación de capacidad antes de confirmar.
- No agregues operaciones remotas síncronas a la ruta crítica de una reserva salvo que una especificación lo autorice y describa la degradación.
- Propaga `correlationId` de HTTP a eventos, logs y respuestas diagnosticables. No registres secretos ni información sensible.

## Convenciones de código

- DTO HTTP y eventos: `record`; contrato REST: interfaz OpenAPI; construcción por `@RequiredArgsConstructor`; mapeo en MapStruct.
- Español sin `ñ`, nombres expresivos, Javadoc en tipos/métodos públicos, complejidad ciclomática máxima 8 y sin comentarios que reemplacen código legible.
- Errores centralizados, internacionalizados mediante `message.properties` y expuestos como `RespuestaGeneral<T>` sin filtrar claves internas.
- Toda migración SQL documenta tablas y columnas mediante `COMMENT ON`; nunca se modifica una migración aplicada.

## Pruebas

- Prueba casos de uso sin Spring cuando sea posible; usa integración para adaptadores, Flyway, PostgreSQL y mensajería cuando aplique.
- Mantén pruebas ArchUnit de hexágono y tests de contrato de eventos. Verifica duplicados, reintentos, compensación y concurrencia en flujos críticos.
