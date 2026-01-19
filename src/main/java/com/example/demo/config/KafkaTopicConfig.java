package com.example.demo.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class KafkaTopicConfig {

    /**
     * In der Zentrale werden Topics explizit angelegt:
     * - Pro Warehouse eine "Queue" (Kafka Topic): warehouse.<id>.stock
     * - Ein Ack-Topic (Kafka Topic): warehouse.ack
     *
     * Mehrere Topics als ein Bean via KafkaAdmin.NewTopics (Spring Kafka >= 2.7). :contentReference[oaicite:3]{index=3}
     */
    @Bean
    @ConditionalOnProperty(name = "app.role", havingValue = "central")
    public KafkaAdmin.NewTopics centralTopics(AppProperties props) {
        List<NewTopic> topics = new ArrayList<>();

        topics.add(TopicBuilder.name(props.getTopics().getAck())
                .partitions(1)
                .replicas(1)
                .build());

        for (String id : props.getWarehouseIds()) {
            topics.add(TopicBuilder.name(props.stockTopicFor(id))
                    .partitions(1)
                    .replicas(1)
                    .build());
        }

        return new KafkaAdmin.NewTopics(topics.toArray(new NewTopic[0]));
    }
}
