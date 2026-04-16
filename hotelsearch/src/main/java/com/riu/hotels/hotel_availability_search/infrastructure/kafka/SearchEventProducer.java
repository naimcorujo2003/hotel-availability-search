package com.riu.hotels.hotel_availability_search.infrastructure.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import com.riu.hotels.hotel_availability_search.domain.model.HotelSearch;
import com.riu.hotels.hotel_availability_search.domain.port.out.SearchEventPublisher;
import com.riu.hotels.hotel_availability_search.infrastructure.mapper.SearchMapper;

@Component
public class SearchEventProducer implements SearchEventPublisher{

    private final KafkaTemplate<String, SearchEventMessage> kafkaTemplate;
    private final SearchMapper searchMapper;

    public SearchEventProducer(KafkaTemplate<String, SearchEventMessage> kafkaTemplate,
                                SearchMapper searchMapper) {
    
        this.kafkaTemplate = kafkaTemplate;
        this.searchMapper = searchMapper;
    } 

    @Override
    public void publish(HotelSearch hotelSearch) {
        SearchEventMessage message = searchMapper.toEventMessage(hotelSearch);
        kafkaTemplate.send(KafkaConfig.TOPIC_HOTEL_AVAILABILITY_SEARCHES,
                hotelSearch.searchId(), message);
    }
    
}
