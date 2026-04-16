package com.riu.hotels.hotel_availability_search.infrastructure.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    
    public static final String TOPIC_HOTEL_AVAILABILITY_SEARCHES = "hotel_availability_searches";

    @Bean
    public NewTopic hotelAvailabilitySearchesTopic() {
        return TopicBuilder.name(TOPIC_HOTEL_AVAILABILITY_SEARCHES)
            .partitions(1)
            .replicas(1)
            .build();
    }
}
