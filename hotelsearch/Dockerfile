FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml . 
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
COPY wait-for-oracle.sh wait-for-oracle.sh
RUN chmod +x wait-for-oracle.sh
ENTRYPOINT ["./wait-for-oracle.sh"]