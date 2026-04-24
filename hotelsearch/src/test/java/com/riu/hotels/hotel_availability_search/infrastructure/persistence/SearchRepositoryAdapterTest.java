package com.riu.hotels.hotel_availability_search.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.riu.hotels.hotel_availability_search.domain.model.HotelSearch;
import com.riu.hotels.hotel_availability_search.infrastructure.mapper.SearchMapper;
import com.riu.hotels.hotel_availability_search.infrastructure.persistence.entity.HotelSearchEntity;

@ExtendWith(MockitoExtension.class)
public class SearchRepositoryAdapterTest {

    @Mock
    private SearchJpaRepository jpaRepository;

    @Mock
    private SearchMapper searchMapper;

    @InjectMocks
    private SearchRepositoryAdapter searchRepositoryAdapter;

    @Test
    void save_shouldCallJpaRepository() {
        HotelSearch hotelSearch = new HotelSearch(
                "search-123", "hotel-456",
                LocalDate.of(2024, 1, 10),
                LocalDate.of(2024, 1, 15),
                List.of(30, 25)
        );
        HotelSearchEntity entity = new HotelSearchEntity(
                "search-123", "hotel-456",
                LocalDate.of(2024, 1, 10),
                LocalDate.of(2024, 1, 15),
                List.of(30, 25)
        );
        when(searchMapper.toEntity(hotelSearch)).thenReturn(entity);
        searchRepositoryAdapter.save(hotelSearch);
        verify(jpaRepository).save(entity);
    }

    @Test
    void findBySearchId_shouldReturnHotelSearch_whenExists() {
        HotelSearchEntity entity = new HotelSearchEntity(
                "search-123", "hotel-456",
                LocalDate.of(2024, 1, 10),
                LocalDate.of(2024, 1, 15),
                List.of(30, 25)
        );
        HotelSearch hotelSearch = new HotelSearch(
                "search-123", "hotel-456",
                LocalDate.of(2024, 1, 10),
                LocalDate.of(2024, 1, 15),
                List.of(30, 25)
        );
        when(jpaRepository.findById("search-123")).thenReturn(Optional.of(entity));
        when(searchMapper.toDomainFromEntity(entity)).thenReturn(hotelSearch);
        Optional<HotelSearch> result = searchRepositoryAdapter.findBySearchId("search-123");
        assertAll(
            () -> assertThat(result).isPresent(),
            () -> assertThat(result.get().searchId()).isEqualTo("search-123")
        );
    }

    @Test
    void findBySearchId_shouldReturnEmpty_whenNotExists() {
        when(jpaRepository.findById("uknown")).thenReturn(Optional.empty());
        Optional<HotelSearch> result = searchRepositoryAdapter.findBySearchId("uknown");
        assertThat(result).isEmpty();
    }

    @Test
    void countSimilarSearches_shouldReturnCount() {
        HotelSearchEntity entity = new HotelSearchEntity(
                "search-123", "hotel-456",
                LocalDate.of(2024, 1, 10),
                LocalDate.of(2024, 1, 15),
                List.of(30, 25)
        );
        when(jpaRepository.findByHotelIdAndCheckInAndCheckOut(
                "hotel-456",
                LocalDate.of(2024, 1, 10),
                LocalDate.of(2024, 1, 15)))
            .thenReturn(List.of(entity));
        long count = searchRepositoryAdapter.countSimilarSearches(
                "hotel-456",
                LocalDate.of(2024, 1, 10),
                LocalDate.of(2024, 1, 15),
                List.of(30, 25)
        );
        assertThat(count).isEqualTo(1L);
    }

    @Test
    void countSimilarSearches_shouldReturnZero_whenAgesOrderDiffers() {
        HotelSearchEntity entity = new HotelSearchEntity(
                "search-123", "hotel-456",
                LocalDate.of(2024, 1, 10),
                LocalDate.of(2024, 1, 15),
                List.of(30, 25)
        );
        when(jpaRepository.findByHotelIdAndCheckInAndCheckOut(
                "hotel-456",
                LocalDate.of(2024, 1, 10),
                LocalDate.of(2024, 1, 15)))
            .thenReturn(List.of(entity));
        long count = searchRepositoryAdapter.countSimilarSearches(
                "hotel-456",
                LocalDate.of(2024, 1, 10),
                LocalDate.of(2024, 1, 15),
                List.of(25, 30)
        );
        assertThat(count).isEqualTo(0L);
    }
}
