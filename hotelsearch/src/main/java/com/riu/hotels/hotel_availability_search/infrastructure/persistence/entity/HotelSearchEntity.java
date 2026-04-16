package com.riu.hotels.hotel_availability_search.infrastructure.persistence.entity;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;


import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "HOTEL_SEARCHES")
public class HotelSearchEntity {
    
    @Id
    @Column(name = "SEARCH_ID", nullable = false, updatable = false)
    private String searchId;

    @Column(name = "HOTEL_ID", nullable = false)
    private String hotelId;

    @Column(name = "CHECK_IN", nullable = false)
    private LocalDate checkIn;

    @Column(name = "CHECK_OUT", nullable = false)
    private LocalDate checkOut;

    @ElementCollection
    @CollectionTable(
                name = "HOTEL_SEARCH_AGES",
                joinColumns = @JoinColumn(name = "SEARCH_ID")
    )
    @Column(name = "Age")
    @OrderColumn(name = "AGE_INDEX")
    private List<Integer> ages;

    protected HotelSearchEntity() {}

    public HotelSearchEntity(String searchId, String hotelId, LocalDate checkIn,
                                 LocalDate checkOut, List<Integer> ages) {
        this.searchId = searchId;
        this.hotelId = hotelId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.ages = Collections.unmodifiableList(List.copyOf(ages));
    }

    public String getSearchId() { return searchId; }
    public String getHotelId() { return hotelId; }
    public LocalDate getCheckIn() { return checkIn; }
    public LocalDate getCheckOut() { return checkOut; }
    public List<Integer> getAges() { return ages; }

} 
