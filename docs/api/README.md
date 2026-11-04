# Contratos HTTP V2

## Propósito

El API Gateway será el punto de entrada para el frontend. Los microservicios no exponen sus detalles de persistencia ni obligan al frontend a conocer eventos internos.

## Convenciones

- Rutas públicas versionadas bajo `/api/v2/`.
- Los recursos se nombran en plural y usan sustantivos: `/experiencias`, `/reservas`, `/pagos`.
- Las operaciones que cambian estado usan comandos explícitos cuando no representan un CRUD simple: `/reservas/{id}/confirmaciones`, `/reservas/{id}/cancelaciones`.
- Las respuestas mantienen el sobre `RespuestaGeneral<T>` con `codigoHttp`, `codigo`, `mensaje` y `data`.
- Los errores no exponen trazas, secretos, claves internas ni detalles de infraestructura.
- La especificación OpenAPI de cada servicio es el contrato ejecutable; el Gateway publica la superficie consolidada necesaria para el frontend.
- Todo endpoint nuevo debe figurar en el spec funcional que lo requiere y en OpenAPI antes de considerarse terminado.

## Seguridad

Las rutas públicas solo permiten explorar el catálogo. Las operaciones de perfil, reserva, pago, operación y administración requieren un access token válido. La autorización se decide por permisos del caso de uso, no por visibilidad de la pantalla.

## Relación con eventos

Una respuesta HTTP confirma solamente el resultado del comando local. Las consecuencias asíncronas, como notificación o actualización de una proyección, se expresan mediante eventos y pueden quedar disponibles después.
