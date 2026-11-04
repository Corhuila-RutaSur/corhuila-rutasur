# 008 — Calificaciones

**Estado:** Pendiente  
**Servicio dueño:** Reservas y Consultas  
**Origen V1:** Spec 007.

Una reserva finalizada permite una calificación por comprador. Reservas valida elegibilidad y conserva el hecho; publica `CalificacionRegistrada`. Consultas/Catálogo mantienen el promedio de presentación como proyección eventual, nunca como condición para registrar la calificación.
