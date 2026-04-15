package com.riu.hotels.hotel_availability_search.domain.model;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public record HotelSearch (
    
    String searchId,
    String hotelId,
    LocalDate checkIn,
    LocalDate checkOut,
    List<Integer> ages
) {
    public HotelSearch {
        ages = Collections.unmodifiableList(List.copyOf(ages));
    }
}

