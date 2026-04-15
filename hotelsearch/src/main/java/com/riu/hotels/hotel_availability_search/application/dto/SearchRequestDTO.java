package com.riu.hotels.hotel_availability_search.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;


public record SearchRequestDTO(
    
    @NotBlank(message = "Hotel id is required")
    String hotelId,

    @NotNull(message = "Checkin is required")
    LocalDate checkIn,

    @NotNull(message = "CheckOut is required")
    LocalDate checkOut,

    @NotEmpty(message = "Ages must not be empty")
    List<Integer> ages

) {
    public SearchRequestDTO {
        if(ages != null) {
            ages = Collections.unmodifiableList(List.copyOf(ages));
        }
    }
}
