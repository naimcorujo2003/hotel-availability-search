# Hotel Availability Search
REST API for hotel availability searrch built with Spring Boot 3.4.4 and Java 21, following hexagonal architecture principles.

## Tech Stack

- Java 21 
- Spring Boot 3.4.4
- Apache Kafka
- Oracle Database XE 21c
- Docker & Docker Compose
- OpenAPI / Swagger

## Architecture

The project follows hexagonal architecture (ports and adapters);

domain/ -> core business logic, no framework dependencies
application/ -> use cases, orchestrates domain
infrastructure/ -> REST controllers, Kafka, Oracle persistence

##Prerequisites

- Docker
- Docker Compose

No aditional installations requires.

## Runnign the aplication

** 1. Clone the repository and navigate to the project folder; **
```bash
cd hotel-availability-search
```

** 2. Start all services; **
```bash
docker-ccompose up --build
```

This command will:
- Build the application using Maven (inside Docker)
- Start Oracle DB for development (port 1521)
- Start Oracle DB for testing (port 1522)
- Start Zookeeper and Kafka (port 9092)
- Start the application (port 8080)

** 3. Verify the application is running;**
```bash
curl http://localhost:8080/swagger-ui.html
```

## API Documentation

Swagger is available at:
http://localhost:8080/swagger-ui.html

OpenAPI spec:
http://localhost:8080/api-docs


##Endpoint

### POST /search

Creates a new hotel availability search and publishesit to kafka.

**Request;**
```json
{
    "hotelId": "123aBc",
    "checkIn": "29/12/2023",
    "checkout": "31/12/2023",
    "ages": [30, 29, 1, 3]
}
```

**Response;**
```json
{
    "searchId": "550e8400-e29b-41d4-a716-446655440000"
}
```

### GET /count?searchId={searchId}

Returns the search details and count of identical searches.

**Response;**
```json
{
    "searchId": "550e8400-e29b-41d4-a716-446655440000",
    "search": {
        "hotelId": "123aBc",
        "checkIn": "29/12/2023",
        "checkout": "31/12/2023",
        "ages": [30, 29, 1, 3]
    },
    "count": 100
}
```

## Runnning tests

```bash
./mvnw test
```


## Coverage report

```bash
./mvnw verify
```

Report available at: `Target/site/jacoco/index.html`


## Security considerations

In a production environment the following would be recommended:
- JWT authentication with Spring Security
- Rate limiting on endpoints
- Kafka consumer origin validation


