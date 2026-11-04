package co.edu.corhuila.rutasur.consultas.adapter.out.messaging;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

@Configuration
public class MensajeriaConfiguracion {
    @Bean
    SqsClient sqsClient(@Value("${app.mensajeria.endpoint}") String e, @Value("${app.mensajeria.region}") String r) {
        return SqsClient.builder().endpointOverride(URI.create(e)).region(Region.of(r)).credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("local", "local"))).build();
    }
}
