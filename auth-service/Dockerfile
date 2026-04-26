# Auth Service - Runtime only (uses pre-built JAR)
FROM eclipse-temurin:25-jre-noble
WORKDIR /app
EXPOSE 8081
COPY target/auth-service-1.0.0.jar app.jar

# Health check
HEALTHCHECK --interval=10s --timeout=5s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8081/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
