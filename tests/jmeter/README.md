# Pruebas JMeter: comparación V1 y V2

Los planes cubren únicamente los escenarios concurrentes relevantes para usuarios finales:

1. `consulta-experiencias-v2.jmx`: lectura pública del catálogo desde Consultas (`8097`).
2. `reserva-concurrente-v2.jmx`: varios clientes reservan cupos sobre **la misma salida** en Reservas (`8093`).
3. `pago-simulado-v2.jmx`: procesa pagos pendientes en Pagos (`8094`).
4. `recorrido-usuario-completo-v2.jmx`: un usuario consulta 20 veces, crea 20 reservas y paga esas mismas 20 reservas, siempre en ese orden.
5. `recorrido-usuario-completo-v1.jmx`: recorrido equivalente en el monolito legacy (`8090`). Inicia sesión, obtiene el JWT y repite **consultar → reservar → pagar** 20 veces.

No se prueba la creación administrativa de catálogo como carga de usuario final.

## Comparación de recorrido V1 vs V2

Los dos planes de recorrido ejecutan la misma unidad funcional en secuencia: una consulta pública del catálogo, creación de una reserva de un participante y pago simulado de esa reserva. Se repite 20 veces con un solo usuario para medir el tiempo de cada paso sin introducir competencia entre usuarios.

Antes de abrir `recorrido-usuario-completo-v1.jmx`:

1. Inicie `legacy-v1` en el puerto `8090`.
2. Cree una experiencia, una salida en estado `ABIERTA` con al menos 20 cupos y una programación de servicio activa para esa salida.
3. En **CONFIGURAR DATOS V1**, reemplace `salidaExperienciaId` y `programacionServicioId` por sus identificadores numéricos reales.
4. Mantenga `usuarioId` alineado con el usuario autenticado. En una base legacy recién creada, el administrador inicial suele tener id `1`.

El plan usa por defecto la cuenta inicial `admin@rutasur.local` con contraseña `RutaSurAdmin2026!`, creada exclusivamente para el entorno académico local. Si usa otra cuenta, reemplace `correo`, `contrasenia` y `usuarioId`.

Para comparar de forma honesta, ejecute ambos recorridos con una salida que tenga cupos suficientes, la misma máquina, un solo hilo, 20 iteraciones y sin otros planes JMeter en ejecución. Conserve la captura del **Summary Report** de cada versión. La V1 y V2 no comparten contratos ni identificadores: V1 usa IDs numéricos y V2 UUID.

## Preparación de reserva concurrente

Antes de ejecutar la reserva, cree manualmente una experiencia, servicio, actividad, salida y sus programaciones. Prepare una salida con pocos cupos (por ejemplo, `5`) para observar competencia real.

En el plan configure los UUID de `salidaId`, `programacionServicioId` y `programacionActividadId`. Además, cree `usuarios-reserva.csv` con una fila por cliente, sin encabezado:

```text
usuarioId,numeroDocumento
UUID_CLIENTE_1,CC1001
UUID_CLIENTE_2,CC1002
```

El número de hilos no debe superar las filas del archivo. Si hay 5 cupos y dispara 10 hilos, el resultado esperado es: cinco respuestas `201` y cinco conflictos `409`; nunca más de cinco reservas confirmadas o pendientes para esa salida.

## Preparación de pagos

Después de crear reservas pendientes, prepare `pagos-pendientes.csv`, sin encabezado:

```text
reservaId,monto
UUID_RESERVA_1,140000.00
```

Cada fila representa un pago simulado. Ejecute el plan de pagos después de la reserva; no los mezcle en la misma prueba si se quiere comparar rendimiento de cada servicio.

## Ejecución no gráfica

```powershell
.\bin\jmeter.bat -n -t "C:\Users\jason\Documents\Repository\corhuila-rutasur\tests\jmeter\consulta-experiencias-v2.jmx" -l consultas.jtl
```

Ejecute los tres planes por separado y conserve sus archivos `.jtl` para la comparación V1 vs V2.

Para Reserva y Pagos indique la ruta absoluta del CSV al ejecutar sin interfaz:

```powershell
.\bin\jmeter.bat -n -t "C:\Users\jason\Documents\Repository\corhuila-rutasur\tests\jmeter\reserva-concurrente-v2.jmx" -JusuariosCsv="C:\ruta\usuarios-reserva.csv" -l reservas.jtl
```
