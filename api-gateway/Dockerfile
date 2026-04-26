# Multi-stage build for API Gateway
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder

WORKDIR /app

# Copy everything from project root (parent context)
COPY . .

# Install parent pom first
RUN mvn install -N -DskipTests -q

# Build common module
RUN mvn -pl common clean install -DskipTests -q

# Build API Gateway
RUN mvn -pl api-gateway clean package -DskipTests -q

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy JAR from builder
COPY --from=builder /app/api-gateway/target/*.jar app.jar

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8000/actuator/health || exit 1

EXPOSE 8000

CMD ["java", "-jar", "app.jar"]
