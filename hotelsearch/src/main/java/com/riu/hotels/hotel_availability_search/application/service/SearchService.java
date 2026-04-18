package com.riu.hotels.hotel_availability_search.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.riu.hotels.hotel_availability_search.application.dto.SearchRequestDTO;
import com.riu.hotels.hotel_availability_search.domain.exception.InvalidDateRangeException;
import com.riu.hotels.hotel_availability_search.domain.model.HotelSearch;
import com.riu.hotels.hotel_availability_search.domain.port.in.SearchUseCase;
import com.riu.hotels.hotel_availability_search.domain.port.out.SearchEventPublisher;
import com.riu.hotels.hotel_availability_search.domain.port.out.SearchRepository;

@Service
public class SearchService implements SearchUseCase{
    
    private static final Logger log = LoggerFactory.getLogger(SearchService.class);
    private final SearchRepository searchRepository;
    private final SearchEventPublisher searchEventPublisher;

    public SearchService(SearchRepository searchRepository, SearchEventPublisher searchEventPublisher) {
        this.searchEventPublisher = searchEventPublisher;
        this.searchRepository = searchRepository;
    }


    /**
     * Create a new hotel availability search
     * The searchId is generated in-memory using UUID to avoid any DB round-trip as required by the business specification
     */
    @Override
    public String search(SearchRequestDTO request) {
        if (!request.checkIn().isBefore(request.checkOut())) {
            throw new InvalidDateRangeException("Checkin must be before checkout");
        }

        String searchId = UUID.randomUUID().toString();
        log.info("Generated searchId: {} for hotelId: {}", searchId, request.hotelId());

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

    /**
     * Returns the search details and count of identical searches
     * Age order matters for the count - [30, 25] and [25, 30] are different searches
     */
    @Override
    public SearchCountResult countSearches(String searchId) {
        log.info("Counting searches for searchId: {}", searchId);
        HotelSearch hotelSearch = searchRepository.findBySearchId(searchId)
            .orElseThrow(() -> new IllegalArgumentException(
                String.format("No search found for searchId: %s", searchId)));


        long count = searchRepository.countSimilarSearches(
            hotelSearch.hotelId(),
            hotelSearch.checkIn(),
            hotelSearch.checkOut(),
            hotelSearch.ages()
        );

        log.info("Found {} similar searches for searchId: {}", count, searchId);

        return new SearchCountResult(hotelSearch, count);
    }
}
