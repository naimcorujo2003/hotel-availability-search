package com.riu.hotels.hotel_availability_search.infrastructure.kafka;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public record SearchEventMessage(

    String searchId,
    String hotelId,
    LocalDate checkIn,
    LocalDate checkOut,
    List<Integer> ages

) {
    public SearchEventMessage {

        if (ages != null) {
            ages = Collections.unmodifiableList(List.copyOf(ages));
        }

    }
    
}
