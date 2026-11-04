package co.edu.corhuila.rutasur.catalogo.adapter.out.messaging;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;

/**
 * Cliente SNS configurable para Floci o AWS.
 */
@Configuration
public class MensajeriaConfiguracion {
    @Bean
    SnsClient snsClient(@Value("${app.mensajeria.endpoint}") String endpoint, @Value("${app.mensajeria.region}") String region) {
        return SnsClient.builder().endpointOverride(URI.create(endpoint)).region(Region.of(region)).credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("local", "local"))).build();
    }
}
