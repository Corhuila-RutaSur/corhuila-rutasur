package co.edu.corhuila.rutasur.notificaciones.adapter.out.messaging;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

/**
 * Cliente SQS compatible con Floci local y configuración AWS externa.
 */
@Configuration
public class MensajeriaConfiguracion {
    @Bean
    SqsClient sqsClient(@Value("${app.mensajeria.endpoint}") String endpoint, @Value("${app.mensajeria.region}") String region) {
        return SqsClient.builder().endpointOverride(URI.create(endpoint)).region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("local", "local"))).build();
    }
}
