# ---------- Build stage ----------
FROM gradle:8.10-jdk17 AS build

WORKDIR /app

# Copy Gradle files first for better Docker layer caching
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

RUN chmod +x gradlew

# Download dependencies
RUN ./gradlew dependencies --no-daemon

# Copy source code
COPY src ./src

# Build Spring Boot JAR
RUN ./gradlew bootJar --no-daemon


# ---------- Runtime stage ----------
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy the generated JAR from build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Render provides the PORT environment variable
EXPOSE 8080

# Start Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]
