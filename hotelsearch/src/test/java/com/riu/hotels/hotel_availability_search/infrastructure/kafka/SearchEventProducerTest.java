package com.riu.hotels.hotel_availability_search.infrastructure.kafka;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import com.riu.hotels.hotel_availability_search.domain.model.HotelSearch;
import com.riu.hotels.hotel_availability_search.infrastructure.mapper.SearchMapper;

import org.springframework.kafka.support.SendResult;

@ExtendWith(MockitoExtension.class)
public class SearchEventProducerTest {
    
    @Mock
    private KafkaTemplate<String, SearchEventMessage> kafkaTemplate;

    @Mock
    private SearchMapper searchMapper;
    
    @InjectMocks
    private SearchEventProducer searchEventProducer;

    @Test
    void publish_shouldSendMessageToKafka() {
        HotelSearch hotelSearch = new HotelSearch(
            "search_123",
            "hotel-456",
            LocalDate.of(2024, 1, 10),
            LocalDate.of(2024, 1, 15),
            List.of(30,25)
            );
        SearchEventMessage message = new SearchEventMessage(
            "search_123",
            "hotel-456",
            LocalDate.of(2024, 1, 10),
            LocalDate.of(2024, 1, 15),
            List.of(30,25)
        );

        ProducerRecord<String, SearchEventMessage> producerRecord =
            new ProducerRecord<>("hotel_availability_searches", message);
        
        RecordMetadata recordMetadata = 
            new RecordMetadata(new TopicPartition("hotel_availability_searches", 0),
                    0, 0, 0, 0, 0);
        
        SendResult<String, SearchEventMessage> sendResult = 
            new SendResult<>(producerRecord, recordMetadata);
        

        when(searchMapper.toEventMessage(hotelSearch)).thenReturn(message);
        when(kafkaTemplate.send(eq("hotel_availability_searches"), eq("search_123"), any(SearchEventMessage.class)))
            .thenReturn(CompletableFuture.completedFuture(sendResult));

        searchEventProducer.publish(hotelSearch);

        verify(kafkaTemplate).send(
            eq("hotel_availability_searches"),
            eq("search_123"),
            eq(message)
        );
        
    }

    @Test
    void searchEventmessage_shouldHandlenULLaGES() {
        SearchEventMessage message = new SearchEventMessage(
            "search_123",
            "hotel-456",
            LocalDate.of(2024, 1, 10),
            LocalDate.of(2024, 1, 15),
            null
        );

        assertThat(message.ages()).isNull();
    }

}
