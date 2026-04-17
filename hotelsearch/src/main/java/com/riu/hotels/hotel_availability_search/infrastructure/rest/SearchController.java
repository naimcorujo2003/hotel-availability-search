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


import jakarta.validation.Valid;


@RestController
@RequestMapping
public class SearchController {
    private final SearchUseCase searchUseCase;
    
    
    public SearchController (SearchUseCase searchUseCase) {
        this.searchUseCase = searchUseCase;
    }

    @PostMapping("/search")
    public ResponseEntity<Map<String, String>> search(
                @Valid @RequestBody SearchRequestDTO request) {
            
        String searchId = searchUseCase.search(request);
        return ResponseEntity.ok(Map.of("searchId", searchId));
    }
    
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
