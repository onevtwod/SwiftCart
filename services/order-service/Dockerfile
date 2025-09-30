# syntax=docker/dockerfile:1
FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace
COPY . .

# Expect to be built from the service dir with gradle wrapper in repo root
# Using gradle in parent might be preferable; for now assume gradle is available in build env

FROM eclipse-temurin:17-jre AS run
WORKDIR /app
COPY build/libs/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/app/app.jar"]
