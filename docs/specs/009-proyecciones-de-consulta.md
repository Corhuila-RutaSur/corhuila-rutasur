# 009 — Proyecciones de consulta

**Estado:** En implementación  
**Servicio dueño:** Consultas  
**Origen V1:** Catálogo público, mis reservas y administración.

Consultas consume eventos de los servicios dueños y construye vistas reconstruibles: catálogo público, detalle de experiencia, mis reservas y tableros administrativos. Las vistas son eventualmente consistentes, no aceptan comandos de negocio y exponen fecha de actualización cuando ayude a comprender la espera.

## Catálogo público implementado

`GET /api/v2/public/experiencias` devuelve experiencias activas ordenadas por nombre desde `experiencia_proyeccion`. `GET /api/v2/public/experiencias/{id}` compone el detalle con servicios, actividades y próximas salidas con cupos desde proyecciones locales. Ningún endpoint público consulta sincrónicamente Catálogo ni Reservas.
# Proyección de galería pública

`imagen_experiencia_proyeccion` pertenece a la base de datos de **Consultas**. Se alimenta exclusivamente con `ImagenExperienciaActualizada` publicado por el Outbox de Catálogo y conserva `imagenId`, `experienciaId`, `ruta`, `principal`, `orden`, `activa` y `actualizadoEn`.

La consulta pública de resumen resuelve la portada activa; el detalle devuelve la galería activa ordenada. La ruta apunta al archivo público servido por Catálogo mientras el entorno sea local. La consistencia entre la escritura y la proyección es eventual y trazable mediante `correlationId`.

