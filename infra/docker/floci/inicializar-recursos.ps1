param(
    [string]$Endpoint = "http://localhost:4566",
    [string]$Region = "us-east-1"
)

$ErrorActionPreference = "Stop"

if (-not (Get-Command aws -ErrorAction SilentlyContinue)) {
    throw "Se requiere AWS CLI para inicializar los recursos locales de Floci. Instálalo o ejecuta los comandos equivalentes desde un contenedor con AWS CLI."
}

$variablesAws = @(
    "AWS_ACCESS_KEY_ID=local",
    "AWS_SECRET_ACCESS_KEY=local",
    "AWS_DEFAULT_REGION=$Region"
)
foreach ($variableAws in $variablesAws) {
    $partes = $variableAws.Split('=', 2)
    Set-Item -Path "Env:$($partes[0])" -Value $partes[1]
}

$topics = @(
    "catalogo.experiencia",
    "reservas.cupo",
    "reservas.reserva",
    "pagos.pago",
    "operacion.salida"
)

$colas = @{
    "catalogo.experiencia" = @("reservas-catalogo", "consultas-catalogo")
    "reservas.cupo" = @("pagos-cupo", "consultas-cupo")
    "reservas.reserva" = @("notificaciones-reserva", "consultas-reserva")
    "pagos.pago" = @("reservas-pago", "notificaciones-pago", "consultas-pago")
    "operacion.salida" = @("reservas-operacion", "consultas-operacion")
}

$topicArns = @{}
foreach ($topic in $topics) {
    $arn = aws --endpoint-url $Endpoint sns create-topic --name $topic --query TopicArn --output text
    $topicArns[$topic] = $arn
}

foreach ($topic in $topics) {
    foreach ($cola in $colas[$topic]) {
        aws --endpoint-url $Endpoint sqs create-queue --queue-name $cola | Out-Null
        $colaUrl = aws --endpoint-url $Endpoint sqs get-queue-url --queue-name $cola --query QueueUrl --output text
        $colaArn = aws --endpoint-url $Endpoint sqs get-queue-attributes --queue-url $colaUrl --attribute-names QueueArn --query 'Attributes.QueueArn' --output text
        aws --endpoint-url $Endpoint sns subscribe --topic-arn $topicArns[$topic] --protocol sqs --notification-endpoint $colaArn | Out-Null
    }
}

Write-Host "Floci inicializado: $($topics.Count) tópicos y colas por consumidor creadas." -ForegroundColor Green
