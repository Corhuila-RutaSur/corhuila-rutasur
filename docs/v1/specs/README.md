# Especificaciones funcionales

Las especificaciones son la fuente de verdad funcional de RutaSur: describen qué debe hacer el sistema, sus reglas y cómo comprobarlas. No imponen detalles internos de código salvo que sean parte de un contrato o una restricción explícita.

## Especificaciones V1

| Documento | Alcance |
| --- | --- |
| [001-glosario-del-dominio](001-glosario-del-dominio.md) | Lenguaje común del dominio turístico. |
| [002-gestion-de-experiencias](002-gestion-de-experiencias.md) | Catálogo de experiencias, imágenes y servicios. |
| [003-gestion-de-salidas](003-gestion-de-salidas.md) | Fechas, cupos, precios y disponibilidad. |
| [004-reservas](004-reservas.md) | Compra configurable, participantes y asistencia. |
| [005-pagos](005-pagos.md) | Registro y efecto de pagos y reembolsos. |
| [006-operacion-y-asistencia](006-operacion-y-asistencia.md) | Ejecución, guía, conductor, vehículo y asistencia. |
| [007-calificaciones](007-calificaciones.md) | Evaluación posterior de una reserva finalizada. |
| [008-autorizacion-y-roles](008-autorizacion-y-roles.md) | Permisos por rol en la V1. |
| [009-reglas-de-negocio](009-reglas-de-negocio.md) | Reglas transversales consolidadas. |
| [010-estados-del-dominio](010-estados-del-dominio.md) | Estados y transiciones permitidas. |
| [011-autenticacion-y-sesiones](011-autenticacion-y-sesiones.md) | Acceso público, JWT, sesiones, refresh tokens y revocación. |
| [012-gobierno-documentacion](012-gobierno-documentacion.md) | Actualización obligatoria de README, specs, modelo, API y Wiki. |
| [013-gestion-de-usuarios](013-gestion-de-usuarios.md) | Registro, perfil, unicidad de datos e inactivación lógica de usuarios. |

Para crear una nueva funcionalidad, utiliza `plantilla-spec.md` y el siguiente número consecutivo.
