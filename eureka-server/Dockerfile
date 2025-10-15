# syntax=docker/dockerfile:1.6

FROM gradle:8.9.0-jdk17 AS build
WORKDIR /workspace
COPY . .
RUN gradle :eureka-server:bootJar --no-daemon -x test

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/eureka-server/build/libs/eureka-server-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java","-jar","/app/app.jar"]

