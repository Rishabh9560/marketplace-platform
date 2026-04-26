# Vendor Service - Runtime only (uses pre-built JAR)
FROM eclipse-temurin:25-jre-noble
WORKDIR /app
EXPOSE 8085
COPY target/vendor-service-1.0.0.jar app.jar

HEALTHCHECK --interval=10s --timeout=5s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8085/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
