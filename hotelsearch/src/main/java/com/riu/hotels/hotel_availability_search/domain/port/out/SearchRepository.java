package com.riu.hotels.hotel_availability_search.domain.port.out;

import java.util.Optional;

import com.riu.hotels.hotel_availability_search.domain.model.HotelSearch;

public interface SearchRepository {
    
    void save(HotelSearch hotelSearch);

    Optional<HotelSearch> findBySearchId(String searchId);

    long countSimilarSearches(String hotelId, java.time.LocalDate checkIn, java.time.LocalDate checkOut, java.util.List<Integer> ages);
}
