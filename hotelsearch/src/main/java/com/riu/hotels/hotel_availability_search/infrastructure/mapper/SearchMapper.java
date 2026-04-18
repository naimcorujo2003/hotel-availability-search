package com.riu.hotels.hotel_availability_search.infrastructure.mapper;

import org.springframework.stereotype.Component;

import com.riu.hotels.hotel_availability_search.domain.model.HotelSearch;
import com.riu.hotels.hotel_availability_search.infrastructure.kafka.SearchEventMessage;
import com.riu.hotels.hotel_availability_search.infrastructure.persistence.entity.HotelSearchEntity;

@Component
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

    public HotelSearchEntity toEntity(HotelSearch hotelSearch) {
        return new HotelSearchEntity(
            hotelSearch.searchId(),
            hotelSearch.hotelId(),
            hotelSearch.checkIn(),
            hotelSearch.checkOut(),
            hotelSearch.ages()
        );
    }

    public HotelSearch toDomainFromEntity(HotelSearchEntity entity) {
        return new HotelSearch(
            entity.getSearchId(),
            entity.getHotelId(),
            entity.getCheckIn(),
            entity.getCheckOut(),
            entity.getAges()
        );
    }
}
