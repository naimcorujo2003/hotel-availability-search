package com.riu.hotels.hotel_availability_search.infrastructure.kafka;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.riu.hotels.hotel_availability_search.domain.model.HotelSearch;
import com.riu.hotels.hotel_availability_search.domain.port.out.SearchRepository;
import com.riu.hotels.hotel_availability_search.infrastructure.mapper.SearchMapper;

@ExtendWith(MockitoExtension.class)
public class SearchEventConsumerTest {
    
    @Mock
    private SearchRepository searchRepository;

    @Mock
    private SearchMapper searchMapper;

    @InjectMocks
    private SearchEventConsumer searchEventConsumer;

    @Test
    void consume_shouldPersistSearch() throws InterruptedException {
        SearchEventMessage message = new SearchEventMessage(
            "search-123",
            "hotel-456",
            LocalDate.of(2024, 1, 10),
            LocalDate.of(2024, 1, 15),
            List.of(30, 25)
        );
        HotelSearch hotelSearch = new HotelSearch(
            "search-123",
            "hotel-456",
            LocalDate.of(2024, 1, 10),
            LocalDate.of(2024, 1, 15),
            List.of(30, 25)
        );

        when(searchMapper.toDomain(message)).thenReturn(hotelSearch);

        searchEventConsumer.consume(message);

        Thread.sleep(100);

        verify(searchRepository).save(hotelSearch);
    }
}
