# syntax=docker/dockerfile:1.6

FROM gradle:8.9.0-jdk17 AS build
WORKDIR /workspace
COPY . .
RUN gradle :gateway:bootJar --no-daemon -x test

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/gateway/build/libs/gateway-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]

