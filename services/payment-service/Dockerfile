# syntax=docker/dockerfile:1
FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace

COPY gradlew gradlew.bat settings.gradle build.gradle /workspace/
COPY gradle /workspace/gradle
COPY services /workspace/services

RUN ./gradlew --no-daemon :services:payment-service:clean :services:payment-service:bootJar -x test

FROM eclipse-temurin:17-jre AS run
WORKDIR /app
COPY --from=build /workspace/services/payment-service/build/libs/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java","-jar","/app/app.jar"]
