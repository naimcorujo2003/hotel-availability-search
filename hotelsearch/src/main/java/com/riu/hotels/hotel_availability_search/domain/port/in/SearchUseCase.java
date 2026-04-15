package com.riu.hotels.hotel_availability_search.domain.port.in;

import com.riu.hotels.hotel_availability_search.application.dto.SearchRequestDTO;
import com.riu.hotels.hotel_availability_search.domain.model.HotelSearch;

public interface SearchUseCase {
    
    String search(SearchRequestDTO request);

    SearchCountResult countSearches(String searchId);

    record SearchCountResult(HotelSearch hotelSearch, long count) {}
}
