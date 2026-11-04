# ADR-001: Usar arquitectura por capas en la V1

**Estado:** Aceptado

## Contexto

RutaSur comienza con un frontend Angular y un backend Spring Boot. La primera versión debe ser clara, fácil de presentar y apropiada para el alcance inicial. No se requiere todavía la complejidad de puertos y adaptadores.

## Decisión

El backend de la V1 se organizará por capas globales: `controller`, `service`, `repository`, `entity`, `dto`, `mapper`, `exception` y `config`. El frontend se organizará por funcionalidades en `features/`, con elementos transversales separados en `core`, `shared` y `layout`.

## Alternativas consideradas

| Alternativa | Ventajas | Desventajas | Decisión |
| --- | --- | --- | --- |
| Arquitectura por capas | Clara, fácil de aprender y adecuada para la V1 | Puede dificultar separar dominios cuando el sistema crece | Elegida para V1 |
| Hexagonal / puertos y adaptadores | Alto desacoplamiento y mayor facilidad de prueba | Añade conceptos y clases innecesarias para el alcance inicial | Pospuesta para una evolución posterior |
| Microservicios desde el inicio | Aislamiento por servicio | Complejidad de despliegue, integración y observabilidad prematura | Descartada |

## Consecuencias

- La trazabilidad de una solicitud será directa: controller, service y repository.
- Los DTOs evitan exponer entidades JPA como contrato público.
- Una evolución hacia arquitectura hexagonal se evaluará solo cuando los requisitos y el tamaño del dominio la justifiquen.
