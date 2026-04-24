package com.riu.hotels.hotel_availability_search.infrastructure.persistence;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.riu.hotels.hotel_availability_search.infrastructure.persistence.entity.HotelSearchEntity;

public interface SearchJpaRepository extends JpaRepository<HotelSearchEntity, String> {
    
    List<HotelSearchEntity> findByHotelIdAndCheckInAndCheckOut( String hotelId, LocalDate checkIn, LocalDate checkOut);
}
