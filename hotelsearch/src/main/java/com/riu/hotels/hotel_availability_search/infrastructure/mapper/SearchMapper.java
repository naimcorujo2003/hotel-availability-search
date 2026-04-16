package com.riu.hotels.hotel_availability_search.infrastructure.mapper;

import com.riu.hotels.hotel_availability_search.domain.model.HotelSearch;
import com.riu.hotels.hotel_availability_search.infrastructure.kafka.SearchEventMessage;

public class SearchMapper {
    
    public SearchEventMessage toEventMessage(HotelSearch hotelSearch) {
        return new SearchEventMessage(
            hotelSearch.searchId(),
            hotelSearch.hotelId(),
            hotelSearch.checkIn(),
            hotelSearch.checkOut(),
            hotelSearch.ages() 
        );
    }

    public HotelSearch toDomain(SearchEventMessage message) {
        return new HotelSearch(
            message.searchId(),
            message.hotelId(),
            message.checkIn(),
            message.checkOut(),
            message.ages()
        );
    }
}
