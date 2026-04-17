package com.riu.hotels.hotel_availability_search;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, topics = "hotel_availability_searches")
@TestPropertySource(properties =  {
	"spring.datasource.url=jdbc:oracle:thin:@localhost:1522/XEPDB1",
	"spring.jpa.hibernate.ddl-auto=create-drop",
	"spring.kafka.consumer.group-id=hotel-availability-group-test"
})
class HotelAvailabilitySearchApplicationTests {

	@Test
	void contextLoads() {
	}

}
