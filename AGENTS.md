# RutaSur: guía de trabajo

## Fuente de verdad

- Lee la especificación aplicable en `docs/specs/` antes de implementar una funcionalidad.
- Lee también la arquitectura y el estándar técnico aplicables en `docs/architecture/` y `docs/standards/` antes de generar código.
- No inventes requisitos de negocio. Registra las dudas o supuestos que cambien el comportamiento.
- Actualiza la especificación cuando cambie el comportamiento público.
- Registra decisiones arquitectónicas duraderas en `docs/adr/`.
- Actualiza el `README.md` en el mismo cambio cuando se alteren el propósito, estado, arquitectura, arranque local, estructura, enlaces principales o forma de usar el proyecto.
- Todo cambio persistente debe mantener alineadas la migración, sus comentarios PostgreSQL y la documentación del modelo de datos.

## Estructura del repositorio

- `apps/web`: aplicación Angular.
- `apps/api`: aplicación Spring Boot.
- `docs/specs`: comportamiento, reglas de negocio y criterios de aceptación.
- `docs/architecture`: arquitectura, contratos y diagramas.
- `docs/adr`: decisiones de arquitectura.
- `docs/standards`: convenciones compartidas.

## Desarrollo

- Mantén las reglas de negocio fuera de controladores REST y componentes Angular.
- Valida las entradas en el servidor; los controles del cliente mejoran la experiencia, pero no sustituyen autorización ni validación del backend.
- Mantén contratos HTTP explícitos: solicitud, respuesta, códigos de estado y errores.
- Aplica los estándares de `docs/standards/`: contrato OpenAPI en interfaces `api/`, MapStruct, complejidad ciclomática máxima de 8, nombres en español sin `ñ`, Javadoc y manejo global internacionalizado de errores.
- Para Angular, aplica `docs/standards/frontend-angular.md`: UI reutilizable en `shared/`, formularios y validadores centralizados, acceso HTTP por servicios, guards y pruebas.
- Prefiere cambios pequeños, cohesionados, verificables y compatibles con los contratos existentes.
- No añadas dependencias, secretos, despliegues, commits o cambios externos sin autorización explícita.

## Calidad y verificación

- Todo comportamiento nuevo debe incluir pruebas proporcionales: caso exitoso, error esperado y caso límite relevante.
- Mantén y ejecuta las pruebas JUnit y ArchUnit aplicables; no introduzcas dependencias entre capas que infrinjan la arquitectura verificada.
- Ejecuta las verificaciones disponibles antes de declarar finalizado un cambio y reporta resultados observados, no supuestos.
- No desactives pruebas, validaciones, controles de seguridad ni reglas de calidad para conseguir una compilación verde.

## Convenciones de documentación

- Usa español para requisitos y documentación de producto; conserva en inglés los identificadores técnicos cuando sea la convención de la tecnología.
- Los criterios de aceptación deben ser observables y verificables.
- Declara explícitamente el alcance y lo que queda fuera de él.
