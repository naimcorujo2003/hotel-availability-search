package com.riu.hotels.hotel_availability_search.infrastructure.persistence;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.riu.hotels.hotel_availability_search.infrastructure.persistence.entity.HotelSearchEntity;

public interface SearchJpaRepository extends JpaRepository<HotelSearchEntity, String> {
    
    @Query("SELECT s FROM HotelSearchEntity s WHERE s.hotelId = :hotelId AND s.checkIn = :checkIn AND s.checkOut = :checkOut")
    List<HotelSearchEntity> findSimilarSearches(
            @Param("hotelId") String hotelId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut);
}
