# ─────────────────────────────────────────────────────────
# Multi-Stage Dockerfile for Task Manager API
#
# Stage 1 (builder): Uses Maven to compile the project and produce a JAR
# Stage 2 (runtime): Uses a lightweight JRE to run the JAR
#
# Multi-stage builds keep the final image SMALL —
# we don't ship Maven or build tools into production.
# ─────────────────────────────────────────────────────────

# ── Stage 1: Build ──────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder

# Set working directory inside the container
WORKDIR /app

# Copy pom.xml first — Docker caches this layer separately.
# If pom.xml hasn't changed, Maven dependencies won't re-download.
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy all source code
COPY src ./src

# Build the JAR, skip tests (tests run in CI, not in Docker build)
RUN mvn clean package -DskipTests -B

# ── Stage 2: Runtime ────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine AS runtime

# Create a non-root user (security best practice)
RUN addgroup -S spring && adduser -S spring -G spring

WORKDIR /app

# Copy only the built JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Switch to non-root user
USER spring:spring

# Expose the application port
EXPOSE 8080

# Set JVM options for container environments (respects container memory limits)
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Start the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
