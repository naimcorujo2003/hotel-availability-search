package com.riu.hotels.hotel_availability_search.infrastructure.kafka;


import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.riu.hotels.hotel_availability_search.domain.model.HotelSearch;
import com.riu.hotels.hotel_availability_search.domain.port.out.SearchRepository;
import com.riu.hotels.hotel_availability_search.infrastructure.mapper.SearchMapper;;


@Component
public class SearchEventConsumer {
    
    private static final Logger log = LoggerFactory.getLogger(SearchEventConsumer.class);

    private final SearchRepository searchRepository;
    private final SearchMapper searchMapper;

    public SearchEventConsumer(SearchRepository searchRepository,
                                SearchMapper searchmapper) {
        this.searchRepository = searchRepository;
        this.searchMapper = searchmapper;
    }
    
    @KafkaListener(
        topics = "hotel_availability_searches",
        groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(SearchEventMessage message) {
        Thread.ofVirtual().start(() -> {
            try {
                HotelSearch hotelSearch = searchMapper.toDomain(message);
                searchRepository.save(hotelSearch);
                log.info("Persisted search {}", hotelSearch.searchId());
            } catch (Exception ex) {
                log.error("Error persisting search: {}", ex.getMessage());
            }
        });
    }

}
