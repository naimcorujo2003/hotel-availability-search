package com.riu.hotels.hotel_availability_search.infrastructure.mapper;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.riu.hotels.hotel_availability_search.domain.model.HotelSearch;
import com.riu.hotels.hotel_availability_search.infrastructure.kafka.SearchEventMessage;
import com.riu.hotels.hotel_availability_search.infrastructure.persistence.entity.HotelSearchEntity;

public class SearchMapperTest {
    
    private SearchMapper searchMapper;

    @BeforeEach
    void setUp() {
        searchMapper = new SearchMapper();
    }

    @Test
    void toEventMessage_shouldMapCorrectly() {
        HotelSearch hotelSearch = new HotelSearch(
            "search-123",
            "hotel-456",
            LocalDate.of(2024, 1, 10),
            LocalDate.of(2024, 1, 15),
            List.of(30,25,5)
        );
        SearchEventMessage message = searchMapper.toEventMessage(hotelSearch);

        assertAll(
            () -> assertThat(message.searchId()).isEqualTo("search-123"),
            () -> assertThat(message.hotelId()).isEqualTo("hotel-456"),
            () -> assertThat(message.ages()).containsExactly(30,25,5)
        );
        
    }

    @Test
    void toDomainFromEntity_shouldMapCorrectly() {
        HotelSearchEntity entity = new HotelSearchEntity(
            "search-123",
            "hotel-456",
            LocalDate.of(2024, 1, 10),
            LocalDate.of(2024, 1, 15),
            List.of(30,25)
        );

        HotelSearch hotelSearch = searchMapper.toDomainFromEntity(entity);

        assertAll(
            () -> assertThat(hotelSearch.searchId()).isEqualTo("search-123"),
            () -> assertThat(hotelSearch.hotelId()).isEqualTo("hotel-456"),
            () -> assertThat(hotelSearch.ages()).containsExactly(30,25)
        );
        
    }

    @Test
    void toDomain_shouldMapCorrectly() {
        SearchEventMessage message = new SearchEventMessage(
            "search-123",
            "hotel-456",
            LocalDate.of(2024, 1, 10),
            LocalDate.of(2024, 1, 15),
            List.of(30, 25, 5)
        );

        HotelSearch hotelSearch = searchMapper.toDomain(message);

        assertAll(
            () -> assertThat(hotelSearch.searchId()).isEqualTo("search-123"),
            () -> assertThat(hotelSearch.hotelId()).isEqualTo("hotel-456"),
            () -> assertThat(hotelSearch.ages()).containsExactly(30, 25, 5)

        );
        
    }

    @Test
    void toEntity_shouldMapCorrectly() {
        HotelSearch hotelSearch = new HotelSearch(
            "search-123",
            "hotel-456",
            LocalDate.of(2024, 1, 10),
            LocalDate.of(2024, 1, 15),
            List.of(30, 25)
        );

        HotelSearchEntity entity = searchMapper.toEntity(hotelSearch);

        assertAll(
            () -> assertThat(entity.getSearchId()).isEqualTo("search-123"),
            () -> assertThat(entity.getHotelId()).isEqualTo("hotel-456"),
            () -> assertThat(entity.getAges()).containsExactly(30, 25)
        );
        
    }
}
