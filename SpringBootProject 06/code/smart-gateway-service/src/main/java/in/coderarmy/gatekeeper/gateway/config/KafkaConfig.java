package in.coderarmy.gatekeeper.gateway.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableKafka
@EnableAsync
public class KafkaConfig {

    @Bean
    public NewTopic gatewayConfigurationTopic(
            @Value("${gateway.kafka.configuration-topic}") String topic) {

        return TopicBuilder.name(topic).partitions(1).replicas(1).build();
    }
}
