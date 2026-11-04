# Carga de datos de prueba V2

Esta guía crea un caso completo y coherente para pruebas funcionales, integración de eventos y carga: una experiencia turística con servicio, actividad, salida, programación, cliente, reserva y pago simulado.

## Antes de ejecutar

Debe haberse completado la [ejecución local V2](ejecucion-local-v2.md). En particular deben estar activos Floci y estos microservicios:

```text
Identidad       http://localhost:8091
Catálogo        http://localhost:8092
Reservas        http://localhost:8093
Pagos           http://localhost:8094
Consultas       http://localhost:8097
```

El script usa los endpoints directos de los microservicios porque el API Gateway todavía no concentra todas las rutas V2 durante la migración. Esta es una excepción local y temporal: consulte el [spec 013 de seguridad de borde](../specs/013-seguridad-de-borde-y-autorizacion-distribuida.md). No modifica V1.

## Ejecución automática recomendada

Desde la raíz del repositorio, en PowerShell:

```powershell
.\infra\scripts\cargar-datos-prueba-v2.ps1
```

Si PowerShell bloquea scripts descargados o locales, use únicamente para esta ejecución:

```powershell
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
.\infra\scripts\cargar-datos-prueba-v2.ps1
```

El script muestra al final todos los UUID generados, el correo del cliente, su contraseña de prueba, el código de reserva y el estado del pago. Cada ejecución agrega datos nuevos con un sufijo de fecha y hora, por lo que no colisiona con la anterior.

## Secuencia de datos creada

| Paso | Servicio dueño | Resultado |
| ---: | --- | --- |
| 1 | Catálogo | Tipo de experiencia de prueba. |
| 2 | Catálogo | Experiencia en Villavieja, Huila. |
| 3 | Catálogo | Servicio de transporte y actividad astronómica. |
| 4 | Reservas | Salida futura con 20 cupos. |
| 5 | Reservas | Programación de servicio por $80.000 y actividad por $60.000. |
| 6 | Identidad | Cliente único con rol `CLIENTE`. |
| 7 | Reservas | Reserva de un participante por $140.000. |
| 8 | Pagos | Pago simulado `APROBADO`. |
| 9 | Consultas | Consulta pública después de esperar la proyección asíncrona. |

El pago es explícito y ocurre después de la reserva; el script conserva esa regla. La aprobación publica el evento correspondiente para que Reservas confirme la reserva de forma asíncrona.

## Pruebas manuales equivalentes

Si se desea usar Postman o Bruno, ejecute las rutas en el mismo orden:

```text
POST http://localhost:8092/api/v2/administracion/tipos-experiencia
POST http://localhost:8092/api/v2/administracion/experiencias
POST http://localhost:8092/api/v2/administracion/experiencias/{experienciaId}/servicios
POST http://localhost:8092/api/v2/administracion/experiencias/{experienciaId}/actividades
POST http://localhost:8093/api/v2/administracion/salidas
POST http://localhost:8093/api/v2/administracion/salidas/{salidaId}/servicios
POST http://localhost:8093/api/v2/administracion/salidas/{salidaId}/actividades
POST http://localhost:8091/api/v2/public/usuarios
POST http://localhost:8093/api/v2/reservas
POST http://localhost:8094/api/v2/pagos
GET  http://localhost:8097/api/v2/public/experiencias
```

Las respuestas usan `RespuestaGeneral<T>`: cada identificador se encuentra en `data.id`. Para una reserva se usan los UUID de las **programaciones** de servicio y actividad, no los UUID directos del catálogo.

## Verificaciones esperadas

- El pago se devuelve inicialmente como `APROBADO`.
- Floci muestra mensajes en los tópicos y colas de `reservas.reserva` y `pagos.pago`.
- La reserva pasa de `PENDIENTE` a `CONFIRMADA` cuando Reservas consume el evento de pago.
- Consultas muestra la experiencia públicamente tras consumir eventos de Catálogo y Reservas.

La propagación es asíncrona: en una máquina local puede tomar algunos segundos. Si una proyección no aparece, revise primero que Floci esté activo, que las colas se hayan inicializado y que los consumidores estén levantados.
