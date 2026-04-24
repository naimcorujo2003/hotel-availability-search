# Hotel Availability Search

[![CircleCI](https://circleci.com/gh/naimcorujo2003/hotel-availability-search.svg?style=shield)](https://circleci.com/gh/naimcorujo2003/hotel-availability-search)

REST API for hotel availability search built with Spring Boot 3.4.4 and Java 21, following hexagonal architecture principles.

## Table of contents

- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Running the application](#running-the-application)
- [API Documentation](#api-documentation)
- [Endpoints](#endpoints)
- [Running tests](#running-tests)
- [Coverage report](#coverage-report)
- [Design decisions](#design-decisions)
- [Things to improve](#things-to-improve)
- [Author](#author)

## Tech Stack

- Java 21
- Spring Boot 3.4.4
- Apache Kafka
- Oracle Database XE 21c
- Docker & Docker Compose
- OpenAPI / Swagger
- CircleCI
- Testcontainers

## Architecture

The project follows hexagonal architecture (ports and adapters) with a single Maven module:

    domain/          -> core business logic, no framework dependencies
    application/     -> use cases, orchestrates domain
    infrastructure/  -> REST controllers, Kafka, Oracle persistence

Dependency rule: infrastructure -> application -> domain. The domain knows nothing about Spring, Kafka, or Oracle.

## Prerequisites

- Docker
- Docker Compose

No additional installations required. The Docker build compiles the application internally.

## Running the application

**1. Clone the repository:**

    git clone https://github.com/naimcorujo2003/hotel-availability-search.git
    cd hotel-availability-search/hotelsearch

**2. Start all services:**

    docker-compose up --build

This command will:
- Build the application using Maven (inside Docker, no local Maven required)
- Start Oracle DB (port 1521) and create the database user automatically
- Start Zookeeper and Kafka (port 9092)
- Start the application (port 8080)

Note: Oracle XE takes approximately 3 minutes to initialize on first run.

**3. Verify the application is running:**

    curl http://localhost:8080/swagger-ui/index.html

## API Documentation

Swagger UI: http://localhost:8080/swagger-ui/index.html

OpenAPI spec: http://localhost:8080/api-docs

## Endpoints

### POST /search

Creates a new hotel availability search, validates the payload and publishes it to Kafka.

Request:

    {
        "hotelId": "1234aBc",
        "checkIn": "29/12/2023",
        "checkOut": "31/12/2023",
        "ages": [30, 29, 1, 3]
    }

Response 200 OK:

    {
        "searchId": "550e8400-e29b-41d4-a716-446655440000"
    }

Response 400 Bad Request:

    {
        "error": "Checkin must be before checkout"
    }

### GET /count?searchId={searchId}

Returns the search details and count of identical searches. Age order is significant — [30, 25] and [25, 30] are counted as different searches.

Response 200 OK:

    {
        "searchId": "550e8400-e29b-41d4-a716-446655440000",
        "search": {
            "hotelId": "1234aBc",
            "checkIn": "29/12/2023",
            "checkOut": "31/12/2023",
            "ages": [30, 29, 1, 3]
        },
        "count": 100
    }

Response 404 Not Found:

    {
        "error": "No search found for searchId: 550e8400-e29b-41d4-a716-446655440000"
    }

## Running tests

Unit tests only:

    ./mvnw test

Unit tests + integration tests (requires Docker):

    ./mvnw verify

Integration tests use Testcontainers to spin up real Oracle and Kafka containers automatically.

## Coverage report

    ./mvnw verify

Report available at: target/site/jacoco/index.html

Current coverage:
- Instructions: 91%
- Branches: 83%
- Lines: 88%
- Methods: 87%

## Design decisions

- **Hexagonal architecture** — the domain layer has zero framework dependencies. Business logic is testable in isolation and infrastructure is fully replaceable without touching the domain.

- **Record types for immutability** — HotelSearch, SearchRequestDTO and SearchEventMessage are Java records. The compiler enforces immutability and the compact constructor handles defensive copies of mutable fields like List<Integer> ages.

- **UUID for searchId generation** — the searchId is generated in-memory using UUID.randomUUID() to avoid any database round-trip, as required by the business specification.

- **Virtual Threads (Java 21)** — the Kafka consumer persists messages using Thread.ofVirtual(). Virtual threads are lightweight and non-blocking, allowing high throughput without exhausting the platform thread pool.

- **Port/Adapter separation** — SearchEventPublisher and SearchRepository are interfaces defined in the domain. Kafka and Oracle are implementation details the domain does not know about. Swapping either requires only replacing the adapter.

- **SearchUseCase in Application layer** — SearchUseCase (port/in) lives in the application layer, keeping the domain free from use case contracts. The controller maps SearchRequestDTO to HotelSearch before calling the use case.

- **Oracle for dev, Testcontainers for tests** — Oracle XE runs in Docker for the development environment. Integration tests use Testcontainers to spin up isolated Oracle instances automatically.

- **@OrderColumn on ages** — the challenge specifies that age order affects the count. @OrderColumn guarantees JPA preserves list order on persistence and retrieval.

- **@JsonFormat on dates** — dates are serialized and deserialized using dd/MM/yyyy format to match the challenge specification exactly.

- **Age order comparison in Java** — similar searches are filtered in the adapter using List.equals(), which compares elements in order. This ensures [30, 25] and [25, 30] are treated as different searches.

- **Oracle user creation via startup script** — init-db.sql is mounted to /opt/oracle/scripts/startup/ and executed automatically by Oracle on first run, creating hotel_user without any manual intervention.

## Things to improve

- Add JWT authentication with Spring Security to protect the endpoints
- Add rate limiting to prevent abuse of the POST /search endpoint
- Add dead letter topic handling for failed Kafka consumer messages
- Add retry logic with exponential backoff in the Kafka consumer
- Extract SearchCountResponse to a dedicated DTO class in the application layer
- Add contract tests with Spring Cloud Contract

## Author

**Antonio Naim Corujo**

- GitHub: [@naimcorujo2003](https://github.com/naimcorujo2003)
