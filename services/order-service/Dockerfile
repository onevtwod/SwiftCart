# syntax=docker/dockerfile:1
FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace

COPY gradlew gradlew.bat settings.gradle build.gradle /workspace/
COPY gradle /workspace/gradle
COPY services /workspace/services

RUN ./gradlew --no-daemon :services:order-service:clean :services:order-service:bootJar -x test

FROM eclipse-temurin:17-jre AS run
WORKDIR /app
COPY --from=build /workspace/services/order-service/build/libs/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/app/app.jar"]
