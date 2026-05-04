FROM gradle:8.10.2-jdk21 AS builder
WORKDIR /workspace

COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts ./
RUN chmod +x gradlew

COPY src src
RUN ./gradlew clean bootJar --no-daemon

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# curl is used by the docker-compose healthcheck.
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

COPY --from=builder /workspace/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
