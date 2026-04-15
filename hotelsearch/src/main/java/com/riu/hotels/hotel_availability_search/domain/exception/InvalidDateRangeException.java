package com.riu.hotels.hotel_availability_search.domain.exception;

public class InvalidDateRangeException extends RuntimeException {
    
    public InvalidDateRangeException(String message) {
        super(message);
    }
}
