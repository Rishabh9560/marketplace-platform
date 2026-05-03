# Multi-stage build for Spring Boot application
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /build

# Copy pom files first
COPY pom.xml .
COPY common/ common/
COPY vendor-service/ vendor-service/

# Install Maven and build
RUN apk add --no-cache maven && \
    mvn -B clean install -DskipTests -q -o 2>/dev/null || \
    mvn -B clean install -DskipTests -q

# Final image
FROM eclipse-temurin:21-jre-alpine

LABEL maintainer="Marketplace Platform Team"
LABEL description="Vendor Service - Marketplace Microservice"
LABEL version="1.0.0"

# Create non-root user
RUN addgroup -S spring && \
    adduser -S spring -G spring

WORKDIR /app

# Copy JAR from builder
COPY --from=builder /build/vendor-service/target/vendor-service-*.jar app.jar

# Set ownership
RUN chown -R spring:spring /app

# Switch to non-root user
USER spring

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD wget --quiet --tries=1 --spider http://localhost:8080/api/v1/actuator/health/liveness || exit 1

# JVM arguments for container optimization
ENV JAVA_OPTS="-server \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -XX:InitiatingHeapOccupancyPercent=35 \
  -XX:+ParallelRefProcEnabled \
  -XX:+AlwaysPreTouch \
  -XX:+UnlockDiagnosticVMOptions \
  -XX:G1SummarizeRSetStatsPeriod=1 \
  -Djava.awt.headless=true \
  -Dspring.jmx.enabled=false \
  -Dspring.boot.telegraf.enabled=true"

# Run application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
