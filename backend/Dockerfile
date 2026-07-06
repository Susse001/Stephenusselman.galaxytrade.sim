# syntax=docker/dockerfile:1.7

# ---------- Build Stage ----------
FROM gradle:8.8-jdk21 AS builder

WORKDIR /app

# Copy Gradle wrapper and build configuration
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

RUN chmod +x gradlew

# Download dependencies (cached until build.gradle changes)
RUN --mount=type=cache,target=/home/gradle/.gradle \
    ./gradlew dependencies --no-daemon || true

# Copy application source
COPY src src

# Build application
RUN --mount=type=cache,target=/home/gradle/.gradle \
    ./gradlew bootJar --no-daemon

# ---------- Runtime Stage ----------
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]