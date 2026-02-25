package com.ordertracking.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean public NewTopic orderStatusUpdatedTopic() {
        return TopicBuilder.name("order.status.updated").partitions(3).replicas(1).build();
    }
    @Bean public NewTopic notificationEmailTopic() {
        return TopicBuilder.name("notification.email.requested").partitions(3).replicas(1).build();
    }
    @Bean public NewTopic notificationSmsTopic() {
        return TopicBuilder.name("notification.sms.requested").partitions(3).replicas(1).build();
    }
    @Bean public NewTopic deliveryLocationTopic() {
        return TopicBuilder.name("delivery.location.updated").partitions(3).replicas(1).build();
    }
}
