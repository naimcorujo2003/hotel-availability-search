package com.riu.hotels.hotel_availability_search.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.riu.hotels.hotel_availability_search.application.dto.SearchRequestDTO;
import com.riu.hotels.hotel_availability_search.application.port.in.SearchUseCase.SearchCountResult;
import com.riu.hotels.hotel_availability_search.domain.exception.InvalidDateRangeException;
import com.riu.hotels.hotel_availability_search.domain.model.HotelSearch;
import com.riu.hotels.hotel_availability_search.domain.port.out.SearchEventPublisher;
import com.riu.hotels.hotel_availability_search.domain.port.out.SearchRepository;

@ExtendWith(MockitoExtension.class)
public class SearchServiceTests {
    @Mock
    private SearchRepository searchRepository;

    @Mock
    private SearchEventPublisher searchEventPublisher;

    @InjectMocks
    private SearchService searchService;

    private HotelSearch validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new HotelSearch(
            "search-123",
            "hotel-123",
            LocalDate.of(2024, 1, 10),
            LocalDate.of(2024, 1, 15),
            List.of(30, 25, 5)
        );
    }

    @Test
    void search_shouldReturnSearchId_whenRequestIsValid() {
        String searchId = searchService.search(validRequest);

        assertThat(searchId).isNotNull().isNotBlank();
        verify(searchEventPublisher, times(1)).publish(any(HotelSearch.class));
    }

    @Test
    void search_shouldThrowException_whenCheckInIsAfterCheckOut() {
        HotelSearch invalidRequest = new HotelSearch(
            "search-123",
            "hotel123",
            LocalDate.of(2024, 1, 15),
            LocalDate.of(2024, 1, 10),
            List.of(30)
        );

        assertThatThrownBy(() -> searchService.search(invalidRequest))
            .isInstanceOf(InvalidDateRangeException.class)
            .hasMessageContaining("Checkin must be before checkout");

        verify(searchEventPublisher, never()).publish(any());
    }

    @Test
    void search_shouldThrowException_whenCheckInEqualsCheckOut() {
        LocalDate sameDate = LocalDate.of(2024,1, 10);
        HotelSearch invaliRequest = new HotelSearch(
            "search-123",
            "hotel123",
            sameDate,
            sameDate,
            List.of(30)
        );

        assertThatThrownBy(() -> searchService.search(invaliRequest))
            .isInstanceOf(InvalidDateRangeException.class);
    }


    @Test
    void countSearches_shouldReturnCountResult_whenSearchIdExists() {
        HotelSearch hotelSearch = new HotelSearch(
            "search123",
            "hotel123",
            LocalDate.of(2024, 1, 10),
            LocalDate.of(2024, 1, 15),
            List.of(30, 25)
        );

        when(searchRepository.findBySearchId("search123"))
            .thenReturn(Optional.of(hotelSearch));
        when(searchRepository.countSimilarSearches(any(), any(), any(), any()))
            .thenReturn(5L);
        
        SearchCountResult result = searchService.countSearches("search123");

        assertAll(
            () -> assertThat(result.count()).isEqualTo(5L),
            () -> assertThat(result.hotelSearch().searchId()).isEqualTo("search123")
        );
        
    }

    @Test
    void countSearches_shouldThrowException_whenSearchIdNotFound() {
        when(searchRepository.findBySearchId("uknown"))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> searchService.countSearches("uknown"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("No search found for searchId: uknown");
    }

    @Test
    void searchRequestDTO_shouldHandleNullAges() {
        SearchRequestDTO dto = new SearchRequestDTO(
            "hotel123",
            LocalDate.of(2024, 1, 10),
            LocalDate.of(2024, 1, 15),
            null
        );
        assertThat(dto.ages()).isNull();
    }

}
