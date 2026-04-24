package com.riu.hotels.hotel_availability_search.infrastructure.kafka;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import com.riu.hotels.hotel_availability_search.domain.model.HotelSearch;
import com.riu.hotels.hotel_availability_search.domain.port.out.SearchEventPublisher;
import com.riu.hotels.hotel_availability_search.infrastructure.mapper.SearchMapper;

@Component
public class SearchEventProducer implements SearchEventPublisher{

    private static final Logger log = LoggerFactory.getLogger(SearchEventProducer.class);

    private final KafkaTemplate<String, SearchEventMessage> kafkaTemplate;
    private final SearchMapper searchMapper;

    public SearchEventProducer(KafkaTemplate<String, SearchEventMessage> kafkaTemplate,
                                SearchMapper searchMapper) {
    
        this.kafkaTemplate = kafkaTemplate;
        this.searchMapper = searchMapper;
    } 

    /**
     * Publishes a hotel availability search event to Kafka
     * The searchId is used as the message key to ensure ordering
     * of events for the same search within a partition
     */
    @Override
    public void publish(HotelSearch hotelSearch) {
        
        log.info("Publishing search event for searchId: {}", hotelSearch.searchId());

        SearchEventMessage message = searchMapper.toEventMessage(hotelSearch);
        
        kafkaTemplate.send(KafkaConfig.TOPIC_HOTEL_AVAILABILITY_SEARCHES,
                hotelSearch.searchId(), message)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish search event for searchId: {} - reason : {}",
                                hotelSearch.searchId(), ex.getMessage());
                    }else {
                        log.info("Successfully published search event for searchId: {} - offset: {}",
                                        hotelSearch.searchId(),
                                        result.getRecordMetadata().offset());
                    }
                });
    }
    
}
