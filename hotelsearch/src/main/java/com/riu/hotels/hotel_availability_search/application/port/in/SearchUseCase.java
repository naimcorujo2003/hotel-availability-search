package com.riu.hotels.hotel_availability_search.application.port.in;

import com.riu.hotels.hotel_availability_search.domain.model.HotelSearch;

public interface SearchUseCase {
    
    String search(HotelSearch hotelSearch);

    SearchCountResult countSearches(String searchId);

    record SearchCountResult(HotelSearch hotelSearch, long count) {}
}
