# Especificaciones funcionales V1

Las especificaciones definen el comportamiento esperado de RutaSur antes de implementar una funcionalidad. Son la fuente de verdad para frontend, backend, pruebas y agentes de inteligencia artificial.

Los documentos editables viven en `docs/specs/` dentro del repositorio principal:

| Especificación | Qué define |
| --- | --- |
| 001 — Glosario del dominio | Significado de experiencia, salida, reserva, participante y demás términos. |
| 002 — Gestión de experiencias | Catálogo, imágenes y servicios seleccionables. |
| 003 — Gestión de salidas | Fechas, precios, cupos y servicios disponibles. |
| 004 — Reservas configurables | Compra de varios cupos, participantes y combinación de servicios. |
| 005 — Pagos | Registro de intentos, aprobaciones, rechazos y reembolsos. |
| 006 — Operación y asistencia | Guía, conductor, vehículo, ejecución y asistencia individual. |
| 007 — Calificaciones | Evaluación posterior a la finalización de una reserva. |
| 008 — Autorización y roles | Capacidades de cliente, administrador, guía y conductor. |
| 009 — Reglas de negocio | Reglas transversales que no pueden contradecirse. |
| 010 — Estados del dominio | Estados y transiciones permitidas. |
| 011 — Autenticación y sesiones | Acceso público, JWT, refresh tokens rotables y cierre de sesión. |
| 012 — Gobierno de la documentación | Mantenimiento obligatorio de README, specs, modelo, API y Wiki. |

Antes de desarrollar una funcionalidad, se debe leer la especificación correspondiente y actualizarla si cambia el comportamiento público.
