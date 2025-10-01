# syntax=docker/dockerfile:1
FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace

COPY gradlew gradlew.bat settings.gradle build.gradle /workspace/
COPY gradle /workspace/gradle
COPY services /workspace/services

RUN ./gradlew --no-daemon :services:notification-service:clean :services:notification-service:bootJar -x test

FROM eclipse-temurin:17-jre AS run
WORKDIR /app
COPY --from=build /workspace/services/notification-service/build/libs/*.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java","-jar","/app/app.jar"]
