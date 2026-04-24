package com.riu.hotels.hotel_availability_search.infrastructure.persistence;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.riu.hotels.hotel_availability_search.domain.model.HotelSearch;
import com.riu.hotels.hotel_availability_search.domain.port.out.SearchRepository;
import com.riu.hotels.hotel_availability_search.infrastructure.mapper.SearchMapper;

@Component
public class SearchRepositoryAdapter implements SearchRepository {
    
    private final SearchJpaRepository jpaRepository;
    private final SearchMapper searchMapper;

    public SearchRepositoryAdapter(SearchJpaRepository jpaRepository,
                                    SearchMapper searchMapper) {
    this.jpaRepository = jpaRepository;
    this.searchMapper = searchMapper;                                    
    }

    @Override
    public void save(HotelSearch hotelSearch) {
        jpaRepository.save(searchMapper.toEntity(hotelSearch));
    }

    @Override
    public Optional<HotelSearch> findBySearchId(String searchId) {
        return jpaRepository.findById(searchId)
                .map(searchMapper::toDomainFromEntity);
    }

    @Override
    public long countSimilarSearches(String hotelId, LocalDate checkIn,
                                    LocalDate checkOut, List<Integer> ages) {
        return jpaRepository.findByHotelIdAndCheckInAndCheckOut(hotelId, checkIn, checkOut)
                .stream()
                .filter(entity -> entity.getAges().equals(ages))
                .count();
    }

}
