<#
 .SYNOPSIS
 Crea un conjunto completo de datos de prueba para RutaSur V2.

 .DESCRIPTION
 Crea tipo, experiencia, servicio, actividad, salida, programaciones, cliente,
 reserva pendiente y pago simulado aprobado. Usa UUID generados por cada servicio.
 Requiere Identidad, Catálogo, Reservas, Pagos, Consultas y Floci en ejecución.
#>

[CmdletBinding()]
param(
    [string]$IdentidadUrl = 'http://localhost:8091',
    [string]$CatalogoUrl = 'http://localhost:8092',
    [string]$ReservasUrl = 'http://localhost:8093',
    [string]$PagosUrl = 'http://localhost:8094',
    [string]$ConsultasUrl = 'http://localhost:8097'
)

$ErrorActionPreference = 'Stop'
$sufijo = (Get-Date -Format 'yyyyMMddHHmmss')
$correlacionId = [guid]::NewGuid().ToString()
$encabezados = @{ 'X-Correlation-Id' = $correlacionId }

function Invocar-RutaSur {
    param(
        [Parameter(Mandatory)] [ValidateSet('Get', 'Post')] [string]$Metodo,
        [Parameter(Mandatory)] [string]$Uri,
        [object]$Cuerpo
    )

    $parametros = @{
        Method      = $Metodo
        Uri         = $Uri
        Headers     = $encabezados
        ContentType = 'application/json'
    }

    if ($null -ne $Cuerpo) {
        $parametros.Body = $Cuerpo | ConvertTo-Json -Depth 8
    }

    return Invoke-RestMethod @parametros
}

Write-Host 'Creando tipo de experiencia...' -ForegroundColor Cyan
$tipo = (Invocar-RutaSur -Metodo Post -Uri "$CatalogoUrl/api/v2/administracion/tipos-experiencia" -Cuerpo @{
        nombre = "ASTROTURISMO_PRUEBA_$sufijo"
        descripcion = 'Tipo generado automáticamente para pruebas locales V2.'
    }).data

Write-Host 'Creando experiencia...' -ForegroundColor Cyan
$experiencia = (Invocar-RutaSur -Metodo Post -Uri "$CatalogoUrl/api/v2/administracion/experiencias" -Cuerpo @{
        tipoExperienciaId = $tipo.id
        nombre = "Noche de estrellas Tatacoa $sufijo"
        destino = 'Villavieja, Huila'
        descripcion = 'Experiencia de prueba con actividad, servicio, salida, reserva y pago simulados.'
        duracionHoras = 5
        recomendaciones = 'Usar ropa cómoda, hidratación y linterna.'
    }).data

Write-Host 'Creando servicio y actividad...' -ForegroundColor Cyan
$servicio = (Invocar-RutaSur -Metodo Post -Uri "$CatalogoUrl/api/v2/administracion/experiencias/$($experiencia.id)/servicios" -Cuerpo @{
        tipo = 'TRANSPORTE'
        nombre = 'Transporte ida y regreso'
        descripcion = 'Traslado local de prueba desde Neiva.'
    }).data

$actividad = (Invocar-RutaSur -Metodo Post -Uri "$CatalogoUrl/api/v2/administracion/experiencias/$($experiencia.id)/actividades" -Cuerpo @{
        nombre = 'Observación astronómica'
        descripcion = 'Actividad nocturna guiada para la prueba V2.'
        duracionHoras = 3
    }).data

Write-Host 'Creando salida y sus programaciones...' -ForegroundColor Cyan
$fechaSalida = (Get-Date).AddDays(30).ToString('yyyy-MM-ddTHH:mm:sszzz')
$salida = (Invocar-RutaSur -Metodo Post -Uri "$ReservasUrl/api/v2/administracion/salidas" -Cuerpo @{
        experienciaIdExterna = $experiencia.id
        experienciaNombre = $experiencia.nombre
        fechaHoraInicio = $fechaSalida
        cuposTotales = 20
    }).data

$programacionServicio = (Invocar-RutaSur -Metodo Post -Uri "$ReservasUrl/api/v2/administracion/salidas/$($salida.id)/servicios" -Cuerpo @{
        servicioIdExterno = $servicio.id
        nombre = $servicio.nombre
        precio = 80000.00
    }).data

$programacionActividad = (Invocar-RutaSur -Metodo Post -Uri "$ReservasUrl/api/v2/administracion/salidas/$($salida.id)/actividades" -Cuerpo @{
        actividadIdExterna = $actividad.id
        nombre = $actividad.nombre
        precio = 60000.00
    }).data

Write-Host 'Creando cliente...' -ForegroundColor Cyan
$numeroAleatorio = Get-Random -Minimum 100000000 -Maximum 999999999
$cliente = (Invocar-RutaSur -Metodo Post -Uri "$IdentidadUrl/api/v2/public/usuarios" -Cuerpo @{
        tipoDocumento = 'CC'
        numeroDocumento = "PRUEBA-$sufijo"
        nombres = 'Cliente'
        apellidos = 'Pruebas'
        correo = "cliente.$sufijo@rutasur.local"
        telefono = "3$numeroAleatorio"
        contrasenia = 'ClientePrueba2026!'
    }).data

Write-Host 'Creando reserva...' -ForegroundColor Cyan
$reserva = (Invocar-RutaSur -Metodo Post -Uri "$ReservasUrl/api/v2/reservas" -Cuerpo @{
        usuarioIdExterno = $cliente.id
        salidaId = $salida.id
        cantidadParticipantes = 1
        participantes = @(
            @{
                nombres = 'Cliente'
                apellidos = 'Pruebas'
                tipoDocumento = 'CC'
                numeroDocumento = "PRUEBA-$sufijo"
            }
        )
        programacionesServicio = @($programacionServicio.id)
        programacionesActividad = @($programacionActividad.id)
    }).data

Write-Host 'Procesando pago simulado aprobado...' -ForegroundColor Cyan
$pago = (Invocar-RutaSur -Metodo Post -Uri "$PagosUrl/api/v2/pagos" -Cuerpo @{
        reservaIdExterna = $reserva.id
        monto = 140000.00
        resultado = 'APROBADO'
    }).data

Write-Host 'Esperando proyecciones de consulta...' -ForegroundColor Cyan
Start-Sleep -Seconds 5
$catalogoPublico = Invocar-RutaSur -Metodo Get -Uri "$ConsultasUrl/api/v2/public/experiencias" -Cuerpo $null

Write-Host ''
Write-Host 'Datos de prueba creados correctamente.' -ForegroundColor Green
[pscustomobject]@{
    CorrelacionId = $correlacionId
    TipoExperienciaId = $tipo.id
    ExperienciaId = $experiencia.id
    ServicioId = $servicio.id
    ActividadId = $actividad.id
    SalidaId = $salida.id
    ProgramacionServicioId = $programacionServicio.id
    ProgramacionActividadId = $programacionActividad.id
    ClienteId = $cliente.id
    CorreoCliente = $cliente.correo
    ContraseniaCliente = 'ClientePrueba2026!'
    ReservaId = $reserva.id
    CodigoReserva = $reserva.codigo
    TotalReserva = $reserva.total
    PagoId = $pago.id
    EstadoPago = $pago.estado
    ExperienciasEnConsulta = @($catalogoPublico.data).Count
} | Format-List
