package com.riu.hotels.hotel_availability_search;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.OracleContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import com.fasterxml.jackson.databind.JsonNode;


import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("integration-test")
public class HotelAvailabilitySearchIT {
    
    @Container
    static OracleContainer oracle = new OracleContainer(
            DockerImageName.parse("gvenzl/oracle-xe:21-slim"))
            .withUsername("hotel_user")
            .withPassword("hotel_pass");

    @Container
    static KafkaContainer kafka = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.9.0"));

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        System.out.println("Oracle JDBC URL: " + oracle.getJdbcUrl());
        System.out.println("Oracle username: " + oracle.getUsername());

        registry.add("spring.datasource.url", oracle::getJdbcUrl);
        registry.add("spring.datasource.username", oracle::getUsername);
        registry.add("spring.datasource.password", oracle::getPassword);
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create");
        registry.add("spring.datasource.driver-class-name", () -> "oracle.jdbc.OracleDriver");
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.OracleDialect");
        registry.add("spring.jpa.show-sql", () -> "true");
        registry.add("spring.jpa.defer-datasource-initialization", () -> "true");
        registry.add("spring.jpa.properties.hibernate.hbm2ddl.auto", () -> "create-drop");
        registry.add("spring.jpa.properties.hibernate.query.srartup_check", () -> "false");

    }


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void fullFlow_postSearch_thenGetCount() throws Exception {
        MvcResult postResult = mockMvc.perform(post("/search")
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
                .andExpect(jsonPath("$.searchId").exists())
                .andReturn();
    
        String responseBody = postResult.getResponse().getContentAsString();
        JsonNode json = objectMapper.readTree(responseBody);
        String searchId = json.get("searchId").asText();

        assertThat(searchId).isNotBlank();

        Thread.sleep(2000);

        mockMvc.perform(get("/count")
                .param("searchId", searchId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.searchId").value(searchId))
                .andExpect(jsonPath("$.search.hotelId").value("123aBc"))
                .andExpect(jsonPath("$.count").value(1));
    
    }
    
}
