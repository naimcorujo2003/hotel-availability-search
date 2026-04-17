package com.riu.hotels.hotel_availability_search.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.riu.hotels.hotel_availability_search.application.dto.SearchRequestDTO;
import com.riu.hotels.hotel_availability_search.domain.exception.InvalidDateRangeException;
import com.riu.hotels.hotel_availability_search.domain.model.HotelSearch;
import com.riu.hotels.hotel_availability_search.domain.port.in.SearchUseCase;
import com.riu.hotels.hotel_availability_search.domain.port.out.SearchEventPublisher;
import com.riu.hotels.hotel_availability_search.domain.port.out.SearchRepository;

@Service
public class SearchService implements SearchUseCase{
    
    private final SearchRepository searchRepository;
    private final SearchEventPublisher searchEventPublisher;

    public SearchService(SearchRepository searchRepository, SearchEventPublisher searchEventPublisher) {
        this.searchEventPublisher = searchEventPublisher;
        this.searchRepository = searchRepository;
    }

    @Override
    public String search(SearchRequestDTO request) {
        if (!request.checkIn().isBefore(request.checkOut())) {
            throw new InvalidDateRangeException("Checkin must be before checkout");
        }

        String searchId = UUID.randomUUID().toString();

        HotelSearch hotelSearch = new HotelSearch(
            searchId,
            request.hotelId(), 
            request.checkIn(),
            request.checkOut(),
            request.ages()
        );

        searchEventPublisher.publish(hotelSearch);

        return searchId;
    }

    @Override
    public SearchCountResult countSearches(String searchId) {
        HotelSearch hotelSearch = searchRepository.findBySearchId(searchId)
            .orElseThrow(() -> new IllegalArgumentException(
                String.format("No search found for searchId: %s", searchId)));


        long count = searchRepository.countSimilarSearches(
            hotelSearch.hotelId(),
            hotelSearch.checkIn(),
            hotelSearch.checkOut(),
            hotelSearch.ages()
        );

        return new SearchCountResult(hotelSearch, count);
    }
}
