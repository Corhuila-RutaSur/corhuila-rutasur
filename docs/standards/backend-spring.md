# Estándar de backend Spring Boot

## Propósito y aplicación

Este documento define cómo implementar código backend en RutaSur. Debe leerse junto con la especificación funcional aplicable y la arquitectura V1 antes de crear o modificar una funcionalidad.

Orden de consulta para una implementación:

1. `docs/specs/`: comportamiento, reglas y criterios de aceptación.
2. `docs/architecture/estructura-del-proyecto.md`: límites entre capas.
3. Este documento: convenciones de implementación Spring.
4. `docs/database/` y migraciones: modelo persistente y reglas de base de datos, cuando el cambio las afecte.

## Capas y contratos

- Los controladores reciben y devuelven DTOs; nunca exponen entidades JPA.
- Los controladores no acceden a repositorios ni contienen reglas de negocio.
- Los servicios implementan los casos de uso, autorización y reglas de negocio. Coordinan repositorios y mappers.
- Los repositorios contienen consultas de persistencia; no contienen decisiones de negocio.
- Las entidades JPA no deben salir de la capa `service`.
- Los DTOs de entrada se validan con Bean Validation y las respuestas HTTP usan DTOs de salida explícitos.
- Cada controlador implementa una interfaz ubicada en `api/` que contiene el contrato HTTP y las anotaciones OpenAPI. El controlador implementa el comportamiento; la interfaz describe el contrato público.
- Cada método expuesto por la interfaz API declara resumen, descripción, respuestas HTTP y modelos relevantes de OpenAPI.

## Idioma, nombres y documentación de código

1. El código de negocio se escribe en español: clases, métodos, variables, constantes y paquetes de dominio usan nombres claros en español.
2. No se usa la letra `ñ` en identificadores técnicos; se reemplaza por `ni`, por ejemplo `anioSalida` y `contraseniaTemporal`.
3. No se usan nombres genéricos como `x`, `y`, `z`, `data`, `temp`, `obj` o `value` cuando exista un nombre que exprese el propósito (`reserva`, `cuposDisponibles`, `fechaSalida`).
4. Todo método escrito manualmente debe incluir Javadoc en español que explique su propósito, parámetros, resultado, excepciones y reglas relevantes. Se exceptúan métodos generados por herramientas y sobrescrituras cuyo contrato externo ya documente completamente el comportamiento.
5. Los comentarios dentro de métodos son breves y explican decisiones, restricciones o motivos no evidentes; no repiten lo que ya expresa un nombre claro.
6. Los literales repetidos o significativos se extraen a constantes, enumeraciones, clases de códigos o propiedades; no se duplican cadenas mágicas.

## Complejidad y reutilización

1. Ningún método debe superar complejidad ciclomática 8.
2. Si un método se aproxima al límite, se divide en métodos privados con una responsabilidad clara o se reorganiza el caso de uso.
3. Si una misma operación aparece en dos métodos de la misma clase, se extrae a un método con nombre de intención.
4. Si una misma operación aparece en más de una clase, se extrae a una utilidad existente o nueva, o a un servicio de dominio cuando la lógica tenga significado de negocio.
5. Las utilidades son estáticas, sin estado y no contienen reglas de negocio que deban permanecer en un servicio.

## Mapeo entre DTO y entidad

1. Las transformaciones entre DTOs y entidades se implementan con **MapStruct** en `mapper/`.
2. Los mappers se declaran como beans de Spring (`componentModel = "spring"`).
3. No se escriben conversiones manuales repetitivas en controladores o servicios.
4. La lógica de negocio, consultas a repositorios, cifrado y cálculos no pertenecen a un mapper.
5. Las actualizaciones parciales deben expresar con claridad qué campos se permiten modificar; no se debe copiar ciegamente un DTO sobre una entidad.

## Lecturas y consultas

1. Para obtener una entidad que será modificada dentro de un caso de uso, el repositorio puede retornar la entidad JPA necesaria.
2. Para listados, búsquedas, paneles o respuestas que no requieren el agregado completo, se deben preferir **proyecciones** o DTOs de consulta.
3. Las consultas deben seleccionar únicamente los datos necesarios para el caso de uso.
4. No se deben resolver problemas de carga con `EAGER` por defecto; se define una estrategia de consulta explícita cuando sea necesaria.
5. Las consultas paginadas deben usar `Page` o `Slice` cuando el volumen lo justifique.

## Escrituras y transacciones

1. Las operaciones de escritura se coordinan desde métodos `@Transactional` de servicio.
2. `save()` es válido para crear entidades nuevas y para persistir cambios de una entidad cargada cuando deben ejecutarse sus invariantes de dominio.
3. `@Modifying` se reserva para actualizaciones o eliminaciones directas, masivas o puntuales que no requieren cargar la entidad completa.
4. Una consulta `@Modifying` debe declarar explícitamente su transacción y documentar qué reglas previas valida el servicio.
5. No se usa `@Modifying` como reemplazo automático de `save()`; ambas opciones tienen propósitos distintos.
6. Los cambios de esquema, datos semilla, índices, restricciones o comentarios PostgreSQL se realizan exclusivamente mediante una nueva migración Flyway.

## Manejo de errores y seguridad

- Las excepciones se controlan exclusivamente desde `ManejadorGlobalExcepciones` (`@RestControllerAdvice`) en `exception/`; los controladores y servicios no construyen respuestas de error HTTP manualmente.
- Cada excepción de negocio porta un código de error estable, no un mensaje literal. Ejemplo: `reserva.no_encontrada`.
- El código de error se resuelve mediante una clase central de extracción de mensajes (`ResolvedorMensajes`) que usa `MessageSource`; ningún mensaje visible al cliente se escribe directamente en Java.
- Los textos de error residen en `src/main/resources/message.properties` como idioma base. Idiomas adicionales usan el patrón `message_{locale}.properties`, por ejemplo `message_en.properties`.
- La respuesta de error estándar debe incluir como mínimo código HTTP, código funcional, mensaje localizado, ruta y fecha/hora; no expone detalles internos.
- No se exponen detalles internos, SQL, trazas, contraseñas, hashes ni tokens.
- Los endpoints protegidos validan autenticación y autorización en el servidor.
- Los endpoints públicos se declaran explícitamente bajo `/api/v1/public/**`; no se hacen públicos por omisión.

## Pruebas mínimas

Cada caso de uso nuevo o modificado debe probar, como mínimo:

1. Caso exitoso.
2. Regla de negocio o autorización rechazada.
3. Caso límite relevante, por ejemplo cupos agotados, transición de estado inválida o datos inexistentes.

## Convenciones de nombres

- Clases: inglés, singular y orientadas al dominio (`ReservaService`, `ExperienciaRepository`).
- Endpoints: sustantivos plurales en minúscula y con guiones solo si mejora legibilidad.
- DTOs: sufijos `Request` y `Response`.
- Proyecciones: sufijo `Projection` o un nombre que exprese la vista (`ReservaResumenProjection`).
- Migraciones: `V{n}__verbo_objeto.sql`, por ejemplo `V4__add_reservation_cancellation_reason.sql`.
