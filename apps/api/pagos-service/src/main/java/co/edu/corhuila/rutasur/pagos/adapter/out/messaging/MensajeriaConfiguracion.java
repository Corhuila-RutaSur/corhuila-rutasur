package co.edu.corhuila.rutasur.pagos.adapter.out.messaging;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sqs.SqsClient;

/**
 * Configura clientes SNS y SQS compatibles con Floci para Pagos.
 */
@Configuration
public class MensajeriaConfiguracion {
    private StaticCredentialsProvider credenciales() {
        return StaticCredentialsProvider.create(AwsBasicCredentials.create("local", "local"));
    }

    @Bean
    SnsClient snsClient(@Value("${app.mensajeria.endpoint}") String endpoint, @Value("${app.mensajeria.region}") String region) {
        return SnsClient.builder().endpointOverride(URI.create(endpoint)).region(Region.of(region)).credentialsProvider(credenciales()).build();
    }

    @Bean
    SqsClient sqsClient(@Value("${app.mensajeria.endpoint}") String endpoint, @Value("${app.mensajeria.region}") String region) {
        return SqsClient.builder().endpointOverride(URI.create(endpoint)).region(Region.of(region)).credentialsProvider(credenciales()).build();
    }
}
