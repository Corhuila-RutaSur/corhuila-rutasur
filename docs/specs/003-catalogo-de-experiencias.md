# 003 — Catálogo de experiencias

**Estado:** Implementado parcialmente  
**Servicio dueño:** Catálogo  
**Origen V1:** Specs 002 y 009.

## Alcance

- Crear, actualizar, consultar e inactivar experiencias.
- Administrar tipo, descripción, destino, duración, recomendaciones, imágenes, servicios y actividades ofertables.
- Publicar catálogo navegable sin autenticación y administrar contenido con rol autorizado.

## Reglas de negocio

1. Las imágenes y servicios pertenecen a una experiencia de Catálogo; una imagen principal se selecciona explícitamente o se asigna de forma determinista.
2. Inactivar una experiencia evita nuevas publicaciones y reservas, sin eliminar evidencia histórica.
3. Catálogo no descuenta cupos ni crea reservas; comunica cambios por eventos versionados.
4. Las imágenes se almacenan en un adaptador de archivos; la base guarda solo metadatos y ruta segura.
5. Una actividad pertenece a una única experiencia.
6. Los servicios y actividades se seleccionan directamente para una salida; no existe una entidad de plan comercial en V2.
7. Una reserva solo puede seleccionar elementos habilitados para su misma salida y todos sus participantes reciben la misma selección.
8. `tipo_experiencia` es el catálogo administrable de clasificaciones turísticas. Su inactivación es lógica: conserva trazabilidad y evita su uso futuro.

## Contratos afectados

| Tipo | Contrato |
|---|---|
| HTTP público | `GET /api/v2/public/experiencias`, `GET /api/v2/public/experiencias/{id}` |
| HTTP administrativo | CRUD de experiencias, imágenes y servicios |
| Eventos publicados | `ExperienciaPublicada`, `ExperienciaActualizada`, `ExperienciaInactivada`, `ServicioExperienciaActualizado` |

## Corte implementado: creación administrativa

El primer corte implementado expone `POST /api/v2/administracion/experiencias`.
Recibe los datos mínimos de una experiencia, normaliza los literales, valida que la
duración sea positiva y persiste el agregado activo. La respuesta sigue el sobre
`RespuestaGeneral<T>` y no expone entidades JPA.

La protección de este contrato se aplicará en el borde central cuando se implemente
el Gateway V2; durante la extracción local el servicio no debe publicarse fuera de
la red de desarrollo. La integración de autorización central es un requisito previo
para habilitar el frontend contra esta ruta.

## Corte implementado: tipos de experiencia

La migración `V3__create_experience_types.sql` crea y documenta el catálogo
`tipo_experiencia`, con sus ocho clasificaciones iniciales. El contrato administrativo
`/api/v2/administracion/tipos-experiencia` permite crear, consultar activos, editar e
inactivar tipos. Los nombres se normalizan a mayúsculas y no puede haber duplicados
activos.

## Corte implementado: CRUD de experiencias

`V4__relate_experience_to_type.sql` relaciona cada experiencia con un tipo activo
mediante `tipo_experiencia_id`; conserva `experiencia.tipo` como nombre histórico y
de lectura. El contrato administrativo permite crear, consultar activas, consultar
detalle, actualizar e inactivar lógicamente experiencias. Al crear o editar, el tipo
debe existir y estar activo. La eliminación física no está permitida.

## Criterios de aceptación

```gherkin
Escenario: Publicar una experiencia
Dado que un administrador autorizado registra los datos obligatorios
Cuando confirma la creación
Entonces Catálogo persiste la experiencia y su outbox en la misma transacción
Y publica un evento que permite actualizar lecturas dependientes.
```

## Pruebas requeridas

- Casos de uso para creación, actualización e inactivación. Existen pruebas unitarias
  de creación, edición, inactivación y duración inválida.
- Integración Flyway/JPA y publicación Outbox.
- Contrato público sin exposición de entidades de persistencia.

## Corte implementado: servicios por experiencia

La migración `V5__complete_experience_services.sql` completa la trazabilidad de
`servicio_experiencia` e impide duplicar el nombre de un servicio dentro de la misma
experiencia. El CRUD vive bajo
`/api/v2/administracion/experiencias/{experienciaId}/servicios`; crea, lista activos,
edita e inactiva lógicamente. Ninguna operación se permite si la experiencia dueña
está inactiva o no existe.

## Corte implementado: actividades por experiencia

La migración `V6__complete_activities.sql` impide repetir el nombre de una actividad
en la misma experiencia. El CRUD se expone en
`/api/v2/administracion/experiencias/{experienciaId}/actividades`; valida duración
positiva, crea, consulta activas, edita e inactiva lógicamente. Una actividad nunca
puede administrarse fuera de su experiencia activa propietaria.
# Galería de imágenes de experiencia

El microservicio **Catálogo** es dueño de los metadatos de `experiencia_imagen`; el binario se almacena localmente en desarrollo mediante `CATALOGO_DIRECTORIO_IMAGENES` y la base de datos guarda únicamente la ruta pública segura. La adaptación a S3 será un cambio de adaptador de salida, no del caso de uso.

- `POST /api/v2/administracion/experiencias/{experienciaId}/imagenes`: recibe `multipart/form-data` con `archivo`, `principal` y `orden`.
- `GET /api/v2/administracion/experiencias/{experienciaId}/imagenes`: devuelve galería administrativa, incluyendo inactivas.
- `PUT /api/v2/administracion/experiencias/{experienciaId}/imagenes/{imagenId}`: cambia portada u orden.
- `DELETE /api/v2/administracion/experiencias/{experienciaId}/imagenes/{imagenId}`: eliminación lógica; conserva el archivo para auditoría y emite el cambio.

Una transacción bloquea pesimistamente la experiencia, retira primero la portada activa y luego marca la nueva. PostgreSQL refuerza la regla con el índice parcial `uk_experiencia_imagen_portada_activa`: nunca puede haber dos portadas activas para la misma experiencia. Si se inactiva la portada, se promueve la primera imagen activa por orden.

Cada cambio registra el evento `ImagenExperienciaActualizada` en Outbox. Consultas lo consume de manera idempotente y reconstruye `imagen_experiencia_proyeccion`; por eso el catálogo público expone `imagenPrincipal` en listados y `imagenes` en el detalle sin realizar llamadas sincrónicas a Catálogo.

