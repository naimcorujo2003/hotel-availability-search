package com.riu.hotels.hotel_availability_search.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    

    @Bean 
    public OpenAPI customOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Hotel Availability Search API")
                        .version("1.0.0")
                        .description("API for searching hotel availability and counting similar searches")
                        .contact(new Contact()
                                    .name("RIU Hotels")
                                    .email("dev@riu.com")));       
    }

}
