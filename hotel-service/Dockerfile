# syntax=docker/dockerfile:1.6

FROM gradle:8.9.0-jdk17 AS build
WORKDIR /workspace
COPY . .
RUN gradle :hotel-service:bootJar --no-daemon -x test

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/hotel-service/build/libs/hotel-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java","-jar","/app/app.jar"]

