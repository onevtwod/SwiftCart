# syntax=docker/dockerfile:1
FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace

# Copy Gradle wrapper and root build files
COPY gradlew gradlew.bat settings.gradle build.gradle /workspace/
COPY gradle /workspace/gradle

# Copy only this service to improve caching
COPY services /workspace/services

# Build the application jar (skip tests for speed in containers)
RUN ./gradlew --no-daemon :services:api-gateway:clean :services:api-gateway:bootJar -x test

FROM eclipse-temurin:17-jre AS run
WORKDIR /app
COPY --from=build /workspace/services/api-gateway/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
