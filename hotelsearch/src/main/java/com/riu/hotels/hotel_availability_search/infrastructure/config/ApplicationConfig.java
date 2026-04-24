package com.riu.hotels.hotel_availability_search.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.riu.hotels.hotel_availability_search.application.port.in.SearchUseCase;
import com.riu.hotels.hotel_availability_search.application.service.SearchService;
import com.riu.hotels.hotel_availability_search.domain.port.out.SearchEventPublisher;
import com.riu.hotels.hotel_availability_search.domain.port.out.SearchRepository;

@Configuration
public class ApplicationConfig {
    
    @Bean
    public SearchUseCase searchUseCase(SearchRepository searchRepository,
                                        SearchEventPublisher searchEventPublisher) {
        
        return new SearchService(searchRepository, searchEventPublisher);
    }
                                        
    
}
