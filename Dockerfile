# Build stage with Maven
FROM maven:3.9-eclipse-temurin-21 as builder

WORKDIR /build

# Copy entire project
COPY . .

# Build vendor-service with dependencies from project root
# -pl: build only vendor-service module
# -am: also-make builds local dependencies first (marketplace-common)
RUN mvn clean package -pl vendor-service -am -DskipTests --quiet

# Final stage
FROM eclipse-temurin:21-jre-alpine

LABEL maintainer="Marketplace Platform Team"
LABEL description="Vendor Service - Marketplace Microservice"

RUN addgroup -S spring && adduser -S spring -G spring

WORKDIR /app

# Copy JAR from builder
COPY --from=builder /build/vendor-service/target/*.jar app.jar

RUN chown -R spring:spring /app
USER spring

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD wget --quiet --tries=1 --spider http://localhost:8080/api/v1/actuator/health/liveness || exit 1

ENV JAVA_OPTS="-server -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:InitiatingHeapOccupancyPercent=35"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

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
