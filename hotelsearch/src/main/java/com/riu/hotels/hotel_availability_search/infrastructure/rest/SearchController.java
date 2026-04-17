package com.riu.hotels.hotel_availability_search.infrastructure.rest;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.riu.hotels.hotel_availability_search.application.dto.SearchRequestDTO;
import com.riu.hotels.hotel_availability_search.domain.model.HotelSearch;
import com.riu.hotels.hotel_availability_search.domain.port.in.SearchUseCase;
import com.riu.hotels.hotel_availability_search.domain.port.in.SearchUseCase.SearchCountResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;


@RestController
@RequestMapping
public class SearchController {
    private final SearchUseCase searchUseCase;
    
    
    public SearchController (SearchUseCase searchUseCase) {
        this.searchUseCase = searchUseCase;
    }

    @Operation(summary = "Create a new hotel search",
                description = "Validate the playload, publishes to Kafka and returns a unique searchId")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Search created successfully"),
        @ApiResponse(responseCode = "404", description = "Invalid request playload")
    })            
    @PostMapping("/search")
    public ResponseEntity<Map<String, String>> search(
                @Valid @RequestBody SearchRequestDTO request) {
            
        String searchId = searchUseCase.search(request);
        return ResponseEntity.ok(Map.of("searchId", searchId));
    }
    

    @Operation(summary = "Count similar searches",
                description = "Returns the search details and count of identical searches")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Search found"),
        @ApiResponse(responseCode = "404", description = "Search not found")
    })
    @GetMapping("/count")
    public ResponseEntity<SearchCountResponse> count(
            @RequestParam String searchId) {

        SearchCountResult result = searchUseCase.countSearches(searchId);
        HotelSearch hs = result.hotelSearch();   
        
        return ResponseEntity.ok(new SearchCountResponse(
                result.hotelSearch().searchId(),
                new SearchCountResponse.SearchDetail(
                    hs.hotelId(),
                    hs.checkIn().toString(),
                    hs.checkOut().toString(),
                    hs.ages()
                ),
                result.count()
        ));

    }

    record SearchCountResponse(
        String searchId,
        SearchDetail search,
        long count
    ) {
        record SearchDetail(
            String hotelId,
            String checkIn,
            String checkOut,
            java.util.List<Integer> ages
        ) {}
    }

    

}
