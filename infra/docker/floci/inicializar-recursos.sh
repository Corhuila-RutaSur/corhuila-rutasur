#!/usr/bin/env bash
set -euo pipefail

ENDPOINT="${FLOCI_ENDPOINT:-http://host.docker.internal:4566}"
REGION="${AWS_REGION:-us-east-1}"

aws_local() {
  docker run --rm \
    -e AWS_ACCESS_KEY_ID=local \
    -e AWS_SECRET_ACCESS_KEY=local \
    -e AWS_DEFAULT_REGION="$REGION" \
    amazon/aws-cli:2.39.3 \
    --endpoint-url "$ENDPOINT" "$@"
}

topics=(catalogo.experiencia reservas.cupo reservas.reserva pagos.pago operacion.salida)
declare -A colas=(
  [catalogo.experiencia]="reservas-catalogo consultas-catalogo"
  [reservas.cupo]="pagos-cupo consultas-cupo"
  [reservas.reserva]="consultas-reserva notificaciones-reserva operacion-reserva pagos-reserva"
  [pagos.pago]="reservas-pago notificaciones-pago consultas-pago"
  [operacion.salida]="reservas-operacion consultas-operacion"
)
declare -A topic_arns

for topic in "${topics[@]}"; do
  topic_arns[$topic]="$(aws_local sns create-topic --name "$topic" --query TopicArn --output text)"
done

for topic in "${topics[@]}"; do
  for cola in ${colas[$topic]}; do
    aws_local sqs create-queue --queue-name "$cola" >/dev/null
    cola_url="$(aws_local sqs get-queue-url --queue-name "$cola" --query QueueUrl --output text)"
    cola_arn="$(aws_local sqs get-queue-attributes --queue-url "$cola_url" --attribute-names QueueArn --query 'Attributes.QueueArn' --output text)"
    suscripcion="$(aws_local sns list-subscriptions-by-topic \
      --topic-arn "${topic_arns[$topic]}" \
      --query "Subscriptions[?Endpoint=='${cola_arn}'].SubscriptionArn | [0]" \
      --output text)"

    if [[ -z "$suscripcion" || "$suscripcion" == "None" ]]; then
      aws_local sns subscribe --topic-arn "${topic_arns[$topic]}" --protocol sqs --notification-endpoint "$cola_arn" >/dev/null
    fi
  done
done

echo "Floci inicializado: tópicos y colas por consumidor disponibles en $ENDPOINT"
