# Estándar frontend Angular V2

- En producción la UI consume exclusivamente el API Gateway; no conoce URLs ni puertos de microservicios internos.
- Durante la transición académica local, mientras el Gateway distribuido no esté implementado, `core/config/api.config.ts` centraliza los puertos directos: Identidad `8091`, Catálogo `8092`, Reservas `8093`, Pagos `8094` y Consultas `8097`. Ningún componente construye estas URLs.
- Los componentes reutilizables viven en `shared/`: botones, campos, validadores/mensajes, iconos, mascota, cards, paneles, modal y toastr; deben ser accesibles, responsivos y consistentes.
- Formularios reactivos: etiqueta visible, validadores centralizados, mensaje debajo del campo, estado de carga y botón reactivado tras error.
- Servicios Angular concentran HTTP y traducen `RespuestaGeneral<T>`; componentes no interpretan códigos internos ni construyen URLs manuales.
- Guards/interceptor manejan sesión, roles y renovación; autorización final siempre es backend.
- Para proyecciones CQRS, comunica consistencia eventual sin mostrar éxito ficticio.
- Pruebas verifican accesibilidad, carga/error, navegación protegida y contrato de servicios.

## Flujos V2 disponibles en la interfaz

1. Catálogo y detalle público consultan `consultas-service`; las cards usan la portada y el detalle presenta la galería.
2. Al seleccionar una salida, el detalle consulta su proyección de disponibilidad: cupos, servicios programados, actividades y precios. La interfaz muestra un estimado, pero la decisión de cupos ocurre siempre en Reservas.
3. La creación de reserva envía UUID de salida, UUID de programaciones y participantes a `reservas-service`. El pago simulado va luego a `pagos-service` y la pantalla **Mis reservas** se reconstruye desde `GET /api/v2/reservas/usuarios/{usuarioId}`.
4. Administración de experiencias crea el agregado en Catálogo y adjunta imágenes mediante `multipart/form-data`; la primera imagen seleccionada se publica como portada.
5. El configurador administrativo no consulta V1: crea y lista servicios en Catálogo; crea y lista salidas en Reservas; y programa cada servicio en una salida con su precio y cupos mediante Reservas.
