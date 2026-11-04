param(
    [string]$DirectorioBase = $PSScriptRoot
)

$directorioIconos = Join-Path $DirectorioBase 'images/aws'
$salida = Join-Path $DirectorioBase 'images/despliegue-aws-v2.svg'

function ObtenerIconoBase64([string]$nombre) {
    [Convert]::ToBase64String([IO.File]::ReadAllBytes((Join-Path $directorioIconos $nombre)))
}

function Icono([int]$x, [int]$y, [string]$archivo, [string]$etiqueta) {
    $datos = ObtenerIconoBase64 $archivo
    @"
  <image x="$x" y="$y" width="64" height="64" href="data:image/svg+xml;base64,$datos"/>
  <text x="$($x + 32)" y="$($y + 84)" class="label" text-anchor="middle">$etiqueta</text>
"@
}

$route53 = Icono 105 370 'Arch_Amazon-Route-53_48.svg' 'Amazon Route 53'
$cloudfront = Icono 270 265 'Arch_Amazon-CloudFront_48.svg' 'Amazon CloudFront'
$s3Frontend = Icono 455 165 'Arch_Amazon-Simple-Storage-Service_48.svg' 'S3 · Angular web'
$apiGateway = Icono 455 400 'Arch_Amazon-API-Gateway_48.svg' 'Amazon API Gateway'
$alb = Icono 865 390 'Arch_Elastic-Load-Balancing_48.svg' 'Application Load Balancer'
$identidad = Icono 690 590 'Arch_AWS-Fargate_48.svg' 'Identidad'
$catalogo = Icono 815 590 'Arch_AWS-Fargate_48.svg' 'Catálogo'
$reservas = Icono 940 590 'Arch_AWS-Fargate_48.svg' 'Reservas'
$pagos = Icono 1090 590 'Arch_AWS-Fargate_48.svg' 'Pagos'
$operacion = Icono 1215 590 'Arch_AWS-Fargate_48.svg' 'Operación'
$consultas = Icono 1340 590 'Arch_AWS-Fargate_48.svg' 'Consultas CQRS'
$notificaciones = Icono 1215 705 'Arch_AWS-Fargate_48.svg' 'Notificaciones'
$auroraWriter = Icono 835 845 'Arch_Amazon-Aurora_48.svg' 'Aurora writer'
$auroraReader = Icono 1265 845 'Arch_Amazon-Aurora_48.svg' 'Aurora readers'
$sns = Icono 1595 290 'Arch_Amazon-Simple-Notification-Service_48.svg' 'Amazon SNS · tópicos'
$sqs = Icono 1595 450 'Arch_Amazon-Simple-Queue-Service_48.svg' 'Amazon SQS · por consumidor'
$dlq = Icono 1750 450 'Arch_Amazon-Simple-Queue-Service_48.svg' 'SQS DLQ'
$s3Imagenes = Icono 1595 650 'Arch_Amazon-Simple-Storage-Service_48.svg' 'S3 · imágenes'
$secrets = Icono 1595 810 'Arch_AWS-Secrets-Manager_48.svg' 'Secrets Manager'
$cloudwatch = Icono 1750 810 'Arch_Amazon-CloudWatch_48.svg' 'Amazon CloudWatch'

$svg = @"
<svg xmlns="http://www.w3.org/2000/svg" width="1920" height="1120" viewBox="0 0 1920 1120">
<defs>
  <style>
    .title { font: 700 23px Arial, sans-serif; fill: #232F3E; }
    .subtitle { font: 600 15px Arial, sans-serif; fill: #232F3E; }
    .label { font: 13px Arial, sans-serif; fill: #232F3E; }
    .small { font: 12px Arial, sans-serif; fill: #4B5563; }
    .group { fill: #ffffff; stroke: #232F3E; stroke-width: 2; }
    .region { fill: #fbfeff; stroke: #147EBA; stroke-width: 2; stroke-dasharray: 8 5; }
    .vpc { fill: #fbfff8; stroke: #7AA116; stroke-width: 2; stroke-dasharray: 8 5; }
    .az { fill: #f8fcf3; stroke: #7AA116; stroke-width: 1.5; stroke-dasharray: 6 4; }
    .subnetPublic { fill: #faf8ff; stroke: #7F5AF0; stroke-width: 1.5; stroke-dasharray: 6 4; }
    .subnetPrivate { fill: #f1fcfd; stroke: #0097A7; stroke-width: 1.5; stroke-dasharray: 6 4; }
    .subnetData { fill: #fff7fc; stroke: #C2297D; stroke-width: 1.5; stroke-dasharray: 6 4; }
    .arrow { fill: none; stroke: #232F3E; stroke-width: 2; marker-end: url(#arrow); }
    .async { fill: none; stroke: #7C3AED; stroke-width: 2; stroke-dasharray: 7 5; marker-end: url(#arrow); }
  </style>
  <marker id="arrow" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="7" markerHeight="7" orient="auto-start-reverse"><path d="M 0 0 L 10 5 L 0 10 z" fill="#232F3E"/></marker>
</defs>
<rect width="1920" height="1120" fill="white"/>
<text x="40" y="42" class="title">RutaSur V2 · Vista de despliegue AWS</text>
<text x="40" y="68" class="small">Arquitectura objetivo: microservicios independientes, red privada, mensajería asíncrona y Aurora por dominio.</text>

<rect x="350" y="95" width="1515" height="960" rx="12" class="group"/>
<text x="375" y="125" class="title">AWS Cloud</text>
<rect x="375" y="145" width="1465" height="880" rx="10" class="region"/>
<text x="400" y="175" class="subtitle">Región AWS · us-east-1</text>
<rect x="610" y="235" width="880" height="730" rx="10" class="vpc"/>
<text x="635" y="265" class="subtitle">VPC RutaSur</text>
<rect x="640" y="295" width="390" height="620" rx="8" class="az"/>
<text x="660" y="322" class="subtitle">Zona de disponibilidad A</text>
<rect x="1070" y="295" width="390" height="620" rx="8" class="az"/>
<text x="1090" y="322" class="subtitle">Zona de disponibilidad B</text>
<rect x="665" y="345" width="340" height="130" rx="8" class="subnetPublic"/>
<text x="685" y="370" class="subtitle">Subred pública</text>
<rect x="1095" y="345" width="340" height="130" rx="8" class="subnetPublic"/>
<text x="1115" y="370" class="subtitle">Subred pública</text>
<rect x="665" y="505" width="340" height="250" rx="8" class="subnetPrivate"/>
<text x="685" y="530" class="subtitle">Subred privada de aplicación</text>
<rect x="1095" y="505" width="340" height="250" rx="8" class="subnetPrivate"/>
<text x="1115" y="530" class="subtitle">Subred privada de aplicación</text>
<rect x="665" y="790" width="340" height="95" rx="8" class="subnetData"/>
<text x="685" y="815" class="subtitle">Subred privada de datos</text>
<rect x="1095" y="790" width="340" height="95" rx="8" class="subnetData"/>
<text x="1115" y="815" class="subtitle">Subred privada de datos</text>

<path d="M 75 402 H 105" class="arrow"/><path d="M 170 402 H 270" class="arrow"/>
<path d="M 334 330 H 455" class="arrow"/><text x="370" y="305" class="small">entrega web</text>
<path d="M 334 330 V 432 H 455" class="arrow"/><text x="370" y="415" class="small">/api</text>
<path d="M 519 432 H 790 V 432 H 865" class="arrow"/><text x="650" y="415" class="small">VPC Link</text>
<path d="M 929 454 V 565 H 976" class="arrow"/><text x="945" y="525" class="small">ALB enruta a ECS Fargate</text>
<path d="M 976 675 V 845 H 899" class="arrow"/><text x="905" y="735" class="small">comandos ACID</text>
<path d="M 899 845 H 1265" class="async"/><text x="1020" y="835" class="small">replicación y consultas</text>
<path d="M 1004 630 H 1595" class="async"/><text x="1330" y="615" class="small">eventos confirmados</text>
<path d="M 1659 382 V 450" class="async"/><path d="M 1659 542 V 705 H 1280" class="async"/>
<path d="M 1659 492 H 1750" class="async"/><text x="1675" y="435" class="small">fan-out</text><text x="1760" y="435" class="small">DLQ</text>
<path d="M 879 630 H 1595" class="async"/><text x="1440" y="665" class="small">imágenes</text>
<path d="M 754 630 H 1595 V 842" class="async"/><path d="M 1404 630 H 1750 V 842" class="async"/>

$route53
$cloudfront
$s3Frontend
$apiGateway
$alb
$identidad
$catalogo
$reservas
$pagos
$operacion
$consultas
$notificaciones
$auroraWriter
$auroraReader
$sns
$sqs
$dlq
$s3Imagenes
$secrets
$cloudwatch
</svg>
"@

Set-Content -LiteralPath $salida -Value $svg -Encoding utf8NoBOM
