package com.riu.hotels.hotel_availability_search.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.riu.hotels.hotel_availability_search.application.port.in.SearchUseCase;
import com.riu.hotels.hotel_availability_search.domain.exception.InvalidDateRangeException;
import com.riu.hotels.hotel_availability_search.domain.model.HotelSearch;
import com.riu.hotels.hotel_availability_search.domain.port.out.SearchEventPublisher;
import com.riu.hotels.hotel_availability_search.domain.port.out.SearchRepository;

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
    public String search(HotelSearch hotelSearch) {
        if (!hotelSearch.checkIn().isBefore(hotelSearch.checkOut())) {
            throw new InvalidDateRangeException("Checkin must be before checkout");
        }

        log.info("Generated searchId: {} for hotelId: {}", hotelSearch.searchId(), hotelSearch.hotelId());

        searchEventPublisher.publish(hotelSearch);

        return hotelSearch.searchId();
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
