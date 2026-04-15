package com.riu.hotels.hotel_availability_search.domain.port.out;

import com.riu.hotels.hotel_availability_search.domain.model.HotelSearch;

public interface SearchEventPublisher {
    
    void publish(HotelSearch hotelSearch);
}
