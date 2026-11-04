# Estándares transversales V2

Los estándares vigentes se aplican a todo trabajo V2. Los de V1 permanecen en `../v1/standards/` como evidencia histórica.

- [Backend de microservicios, hexágono y CQRS](backend-microservicios.md)
- [Pruebas de microservicios](pruebas-microservicios.md)
- [Frontend Angular V2](frontend-angular-v2.md)

## Definición de terminado

Una capacidad V2 está terminada cuando, como mínimo:

1. Tiene un spec con alcance, reglas, escenarios y criterios de aceptación verificables.
2. Respeta el ADR, el límite de propiedad y los contratos HTTP o de eventos correspondientes.
3. Sus cambios de persistencia tienen una migración Flyway nueva y el DBML actualizado.
4. Incluye pruebas unitarias de dominio y de aplicación; agrega pruebas de adaptador o integración cuando el riesgo lo amerite.
5. Incluye trazabilidad con `correlacionId`, logs de decisiones relevantes y manejo de errores sin exponer datos sensibles.
6. Actualiza el README, diagramas, OpenAPI, Wiki o guía de operación si el cambio los afecta.

## Reglas de documentación

- `docs/` es la fuente versionada y revisable; la Wiki replica documentos estables para consulta desde GitHub.
- La V1 se conserva sin reescritura en `docs/v1/` y en el tag `v1`.
- Un cambio funcional no puede integrarse con un spec desactualizado.
- Los diagramas Mermaid son fuente editable; los PNG en `architecture/images/` son la versión de presentación.

## Calidad de diseño

- Preferir cohesión por dominio sobre reutilización técnica prematura entre servicios.
- No compartir entidades de dominio ni repositorios entre microservicios.
- Los adaptadores dependen de puertos; los casos de uso no dependen de frameworks.
- CQRS es selectivo: la lectura se separa cuando aporta una necesidad real de escala, composición o rendimiento.
