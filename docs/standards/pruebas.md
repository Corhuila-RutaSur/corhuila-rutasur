# Estándar de pruebas

## Objetivo

Probar el comportamiento especificado y proteger la arquitectura de RutaSur frente a regresiones. Las pruebas son parte de la definición de terminado de cada cambio.

## Pirámide de pruebas V1

| Nivel | Herramientas | Qué verifica |
| --- | --- | --- |
| Unidad | JUnit 5 + Mockito | Reglas de negocio de servicios, validaciones, transiciones de estado y cálculos. |
| Arquitectura | JUnit 5 + ArchUnit | Dependencias permitidas entre paquetes y capas. |
| Web | Spring MVC Test | Contratos HTTP, validación de DTOs, códigos de estado y seguridad. |
| Persistencia e integración | JPA Test + PostgreSQL Testcontainers | Consultas, proyecciones, restricciones y migraciones Flyway contra PostgreSQL real. |

## Reglas generales

1. Todo caso de uso nuevo tiene, como mínimo, prueba exitosa, prueba de regla rechazada y caso límite relevante.
2. Las pruebas deben expresar comportamiento y usar nombres descriptivos en español sin `ñ`.
3. Una prueba unitaria no inicia el contexto de Spring ni usa una base de datos.
4. Los servicios se prueban con JUnit 5 y Mockito, simulando repositorios, mappers y dependencias externas.
5. Los controladores se prueban con Spring MVC Test, verificando contrato, validación y códigos HTTP.
6. Los repositorios, migraciones y consultas PostgreSQL se prueban contra PostgreSQL mediante Testcontainers; no se sustituye por H2 cuando se validen tipos, SQL o restricciones de PostgreSQL.
7. Las pruebas de arquitectura con ArchUnit se ejecutan en cada compilación y no se desactivan para integrar cambios.
8. Toda falla corregida debe incorporar una prueba que prevenga su reaparición cuando sea viable.

## Reglas iniciales de ArchUnit

`ArquitecturaCapasTest` valida desde el inicio que:

- `controller/` no dependa de `repository/` ni de `entity/`.
- `service/` no dependa de `controller/`.
- `repository/` no dependa de `service/` ni de `controller/`.
- `api/` no dependa de `service/`, `repository/` ni `entity/`.

Las reglas aumentarán junto con la estructura real del proyecto: DTOs, mappers, excepciones y seguridad.

## Calidad adicional obligatoria

- Validación de entradas con Bean Validation en DTOs de solicitud.
- Paginación y ordenamiento explícitos para listados potencialmente grandes; no se aceptan ordenamientos arbitrarios desde el cliente.
- Secretos únicamente mediante variables de entorno o gestor de secretos; nunca en código, pruebas o archivos versionados.
- Logs estructurados y útiles para diagnóstico, sin datos personales innecesarios, contraseñas, hashes, JWT ni refresh tokens.
- Operaciones críticas como pagos, reserva de cupos y renovación de tokens deben ser idempotentes o protegerse contra duplicación y concurrencia.
- Todo cambio de API debe preservar compatibilidad o versionar/coordinar explícitamente el contrato.

## Ejecución

Desde `apps/api`:

```powershell
.\mvnw.cmd test
```

Antes de integrar cambios se ejecutarán, como mínimo, las pruebas afectadas y la suite completa disponible.
