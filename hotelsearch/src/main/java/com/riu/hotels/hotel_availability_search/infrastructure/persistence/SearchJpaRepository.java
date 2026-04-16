package com.riu.hotels.hotel_availability_search.infrastructure.persistence;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.riu.hotels.hotel_availability_search.infrastructure.persistence.entity.HotelSearchEntity;

public interface SearchJpaRepository extends JpaRepository<HotelSearchEntity, String> {
    
    @Query("""
            SELECT COUNT(DISTINCT s.searchId)
            FROM HotelSearchEntity s
            WHERE s.hotelId = :hotelId
            AND s.checkIin = :checkIn
            AND s.checkOut = :checkOut
            ANDs.searchId IN (
                SELECT a.searchId FROM HotelSearchEntity a
                JOIN a.ages age
                GROUP BY a.searchId
                HAVING COUNT(age) = :ageSize
            )
            """)
    long countSimilarSearches(
            @Param("hotelId") String hotelId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut,
            @Param("agesSize") int agesSize
    );
}
