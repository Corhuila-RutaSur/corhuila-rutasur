# Modelo de datos V1

Este documento describe el modelo relacional inicial de RutaSur. La fuente ejecutable es la migracion `V1__create_initial_schema.sql` del backend; este material explica su proposito funcional.

## Idea central

Una **experiencia** es el producto turistico general, por ejemplo *Noche de estrellas en el desierto de la Tatacoa*. Dentro de ella se ofrecen servicios que se pueden combinar libremente: transporte, alojamiento, alimentacion y actividades.

El cliente construye su propio paquete al reservar. La regla que evita combinaciones ilogicas es que una reserva corresponde a una sola salida de una experiencia; por tanto, todos sus servicios pertenecen a esa misma experiencia y fecha.

## Usuarios y roles

| Tabla | Para que se usa |
| --- | --- |
| `tipo_usuario` | Catalogo de roles: `CLIENTE`, `ADMINISTRADOR`, `GUIA` y `CONDUCTOR`. |
| `usuario` | Datos de identidad, contacto y acceso de cada persona registrada. Puede comprar experiencias o trabajar en la operacion. |
| `usuario_tipo` | Tabla puente que asigna uno o varios roles a un usuario. Una persona puede, por ejemplo, ser cliente y guia. |
| `guia` | Perfil operativo de un usuario que acompana una experiencia. Almacena su registro y observaciones. |
| `conductor` | Perfil operativo de un usuario que conduce. Conserva licencia, categoria y vencimiento. |

## Catalogo de experiencias

| Tabla | Para que se usa |
| --- | --- |
| `tipo_experiencia` | Clasificacion turistica amplia: `ASTROTURISMO`, `AVENTURA`, `NATURALEZA`, `CULTURAL`, entre otras. No representa servicios comprables. |
| `experiencia` | Producto turistico principal: nombre, destino, descripcion, duracion y recomendaciones. Ejemplo: *Noche de estrellas en la Tatacoa*. |
| `experiencia_imagen` | Galeria de imagenes de una experiencia; permite indicar una imagen principal y definir orden. |
| `servicio_experiencia` | Cada opcion que el cliente puede comprar dentro de una experiencia. Ejemplos: transporte ida y vuelta, alojamiento, cena, caminata lunar o fogata. |

## Planeacion y disponibilidad

| Tabla | Para que se usa |
| --- | --- |
| `salida_experiencia` | Una realizacion planificada de una experiencia para una fecha y hora concretas; controla cupos y estado. |
| `programacion_servicio` | Habilita un servicio en una salida especifica y define su precio y cupos para esa fecha. Permite que el alojamiento o la caminata tengan valores distintos en otra salida. |

## Reservas y participantes

| Tabla | Para que se usa |
| --- | --- |
| `reserva` | Cabecera de la compra: comprador, salida elegida, codigo, estado, fecha y totales monetarios. Es el paquete armado por el cliente. |
| `participante_reserva` | Cada persona que asistira mediante una reserva. El comprador puede adquirir varios cupos para familiares o amigos. |
| `detalle_reserva` | Cada servicio seleccionado en la reserva, con cantidad, precio congelado al momento de compra y subtotal. |
| `detalle_participante` | Asigna cada servicio comprado a sus participantes y registra la asistencia individual: pendiente, asistio, no asistio o cancelado. |

## Pagos y valoracion

| Tabla | Para que se usa |
| --- | --- |
| `pago` | Intentos y resultados de pago de una reserva: referencia, medio, valor, estado y respuesta de pasarela. Admite reintentos o reembolsos. |
| `calificacion` | Evaluacion general de una reserva terminada: puntuacion de 1 a 5 y comentario. En V1 se permite una por reserva. |

## Ejecucion operativa

| Tabla | Para que se usa |
| --- | --- |
| `vehiculo` | Inventario de vehiculos: placa, tipo, capacidad y estado. Solo interviene cuando hay transporte. |
| `ejecucion_experiencia` | Registro de lo ocurrido realmente en una salida: guia asignado, horas reales, estado y novedades. El guia acompana el flujo completo. |
| `ejecucion_servicio` | Registro operativo de un servicio concreto. Para transporte permite asociar conductor y vehiculo; para caminatas o actividades sin vehiculo esos campos quedan vacios. |

## Autenticacion, sesiones y auditoria

| Tabla | Para que se usa |
| --- | --- |
| `sesion_autenticacion` | Controla la sesion de un usuario: vigencia, ultimo uso, cierre e invalidacion. |
| `refresh_token` | Guarda unicamente el hash de un refresh token de uso unico y permite relacionar su rotacion con el siguiente token. |
| `access_token_revocado` | Conserva el `jti` de un JWT que debe bloquearse antes de su expiracion natural. No guarda el JWT completo. |
| `evento_autenticacion` | Auditoria de inicio de sesion, fallos, renovaciones, reutilizacion de refresh token y cierres de sesion. |

Los visitantes pueden explorar experiencias y salidas publicas. Crear y gestionar reservas requiere una sesion autenticada.

## Tipos de experiencia iniciales

| Tipo | Uso |
| --- | --- |
| `ASTROTURISMO` | Observacion de estrellas, astronomia y actividades nocturnas. |
| `AVENTURA` | Senderismo, actividades de adrenalina y recorridos activos. |
| `NATURALEZA` | Avistamiento, ecoturismo y contacto con el entorno. |
| `CULTURAL` | Historia, patrimonio, artesanias y comunidades locales. |
| `GASTRONOMICA` | Rutas culinarias, degustaciones y cocina local. |
| `BIENESTAR` | Descanso, relajacion y conexion con la naturaleza. |
| `RURAL_AGROTURISMO` | Fincas, procesos campesinos y vida rural. |
| `EVENTO_ESPECIAL` | Festivales, celebraciones y actividades en fechas puntuales. |

## Reglas de negocio protegidas desde la base

1. Una reserva pertenece a una sola `salida_experiencia`.
2. Un servicio agregado a la reserva debe estar programado para esa misma salida.
3. Un servicio solo puede programarse en una salida de su propia experiencia.
4. Un participante solo puede asociarse a detalles de su misma reserva.
5. Un usuario no puede recibir dos veces el mismo rol.
6. Una reserva solo tiene un participante marcado como responsable.
7. La puntuacion de una calificacion esta entre 1 y 5.
8. Los cupos y valores no pueden ser negativos.

La aplicacion tambien debera validar disponibilidad, transiciones de estado, reglas de cancelacion y autorizaciones antes de escribir en la base de datos.

## Migraciones

| Version | Contenido |
| --- | --- |
| `V1` | Estructura inicial: tipos PostgreSQL, tablas, relaciones, restricciones e integridad transversal. |
| `V2` | Datos semilla: roles de usuario y clasificaciones iniciales de experiencias. |
| `V3` | Sesiones, refresh tokens de un solo uso, revocacion de access tokens y auditoria de autenticacion. |

La creacion de la base PostgreSQL `rutasur` no es una migracion Flyway. Debe realizarse una vez por el administrador; luego Flyway aplica las versiones dentro de esa base.

Consulta tambien el [modelo entidad-relacion](../architecture/modelo-entidad-relacion-v1.md) para ver las relaciones completas.
