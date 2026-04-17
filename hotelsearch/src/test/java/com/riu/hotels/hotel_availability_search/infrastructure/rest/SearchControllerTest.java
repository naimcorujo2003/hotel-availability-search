package com.riu.hotels.hotel_availability_search.infrastructure.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.http.MediaType;



import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.riu.hotels.hotel_availability_search.application.dto.SearchRequestDTO;
import com.riu.hotels.hotel_availability_search.domain.model.HotelSearch;
import com.riu.hotels.hotel_availability_search.domain.port.in.SearchUseCase;
import com.riu.hotels.hotel_availability_search.domain.port.in.SearchUseCase.SearchCountResult;
import com.riu.hotels.hotel_availability_search.domain.exception.InvalidDateRangeException;


@WebMvcTest(SearchController.class)
public class SearchControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SearchUseCase searchUseCase;

    @Test
    void postSearch_shouldReturn200_whenRequestIsValid() throws Exception {
        when(searchUseCase.search(any(SearchRequestDTO.class)))
            .thenReturn("generated-search-id");

        mockMvc.perform(post("/search")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                    {
                        "hotelId": "123aBc",
                        "checkIn": "29/12/2023",
                        "checkOut": "31/12/2023",
                        "ages": [30, 29, 1, 3]
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.searchId").value("generated-search-id"));
    }

    @Test
    void postSearch_shouldReturn400_whenCheckInAfterCheckOut() throws Exception {
        when(searchUseCase.search(any(SearchRequestDTO.class)))
            .thenThrow(new InvalidDateRangeException("Checkin must be before checkout"));

        mockMvc.perform(post("/search")
            .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                            {
                                "hotelId": "1234aBc",
                                "checkIn": "30/12/2023",
                                "checkOut": "29/12/2023",
                                "ages": [30]
                            }
                            """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Checkin must be before checkout"));
        
    }

    @Test
    void postSearch_shouldReturn400_whenRequestBodyIsInvalid() throws Exception {
            mockMvc.perform(post("/search")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                            {
                                "hotelId": "",
                                "checkIn": "29/12/2023",
                                "checkOut": "31/12/2023",
                                "ages": [30]
                            }
                            """))
                    .andExpect(status().isBadRequest());
    }

    @Test
    void getCount_shouldReturn200_whenSearchIdExist() throws Exception {
        HotelSearch hotelSearch = new HotelSearch(
            "abc-123",
            "123aBc",
            LocalDate.of(2023, 12, 29),
            LocalDate.of(2023, 12, 31),
            List.of(30, 29, 1, 3));

        when(searchUseCase.countSearches(eq("abc-123")))
            .thenReturn(new SearchCountResult(hotelSearch, 5L));
            
        mockMvc.perform(get("/count")
            .param("searchId", "abc-123"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.searchId").value("abc-123"))
            .andExpect(jsonPath("$.search.hotelId").value("123aBc"))
            .andExpect(jsonPath("$.count").value(5));
    }

    @Test 
    void getCount_shouldReturn404_whenSearchIdNotFound() throws Exception {
        when(searchUseCase.countSearches(eq("uknown")))
            .thenThrow(new IllegalArgumentException("No search found for searchId: uknown"));
        
        mockMvc.perform(get("/count")
                .param("searchId", "uknown"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("No search found for searchId: uknown"));
    }
}
