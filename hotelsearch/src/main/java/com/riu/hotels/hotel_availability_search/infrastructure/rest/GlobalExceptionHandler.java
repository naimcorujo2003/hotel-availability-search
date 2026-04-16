package com.riu.hotels.hotel_availability_search.infrastructure.rest;

import com.riu.hotels.hotel_availability_search.domain.exception.InvalidDateRangeException;

import java.util.Map;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler (InvalidDateRangeException.class)
    public ResponseEntity<Map<String, String>> handleInvalidDateRange(
            InvalidDateRangeException ex) {
            
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(
            MethodArgumentNotValidException ex) {
                
        String message = ex.getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .map(error -> String.format("%s: %s", error.getField(), error.getDefaultMessage()))
                    .findFirst()
                    .orElse("Validation error");
                return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", message));
            }
}
