# Guía de desarrollo

## Flujo Spec-Driven

1. Crear o actualizar la especificación funcional.
2. Definir reglas de negocio, alcance y criterios de aceptación.
3. Diseñar los contratos de API si aplica.
4. Implementar el cambio mínimo necesario.
5. Añadir o actualizar pruebas.
6. Ejecutar verificaciones y registrar evidencia.

## Backend

- Usa DTOs de `request` y `response` para cada endpoint.
- Aplica validaciones con Jakarta Validation en los DTOs de entrada.
- Centraliza las reglas de negocio en `service`.
- Añade migraciones Flyway incrementales; no modifiques una migración ya compartida.
- Documenta endpoints con OpenAPI; Swagger UI y Scalar consumirán el mismo contrato.

## Frontend

- Coloca servicios globales, guards e interceptores en `core`.
- Coloca componentes reutilizables sin negocio en `shared`.
- Coloca el shell de navegación en `layout`.
- Coloca pantallas y lógica exclusiva de cada caso de uso en `features`.

## Calidad

- No publiques secretos ni contraseñas en el repositorio.
- No deshabilites pruebas o validaciones para resolver errores temporalmente.
- Verifica que el backend y frontend compilen antes de abrir un pull request.
