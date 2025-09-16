# Use an official OpenJDK 21 image as the base image
FROM openjdk:21

# Set a working directory inside the container
WORKDIR /app

# Set env
ENV JWT_TOKEN_SECRET = RahasiaRahasia123

# Label the image with metadata
LABEL maintainer="aant <aant@mail.com>"
LABEL version="1.0"
LABEL description="A Java web application running in Docker"

# Copy the compiled JAR file to the container
COPY latihan-java21-spring-restful-api/build/libs/latihan-java21-spring-restful-api.jar /app/latihan-java21-docker.jar

# Expose port 8080 to allow external access
EXPOSE 8080

# Run the application when the container starts
CMD ["java", "-jar", "/app/latihan-java21-docker.jar"]

# Health check to ensure the application is running
HEALTHCHECK --interval=30s --timeout=10s --start-period=10s --retries=3 \
    CMD curl --fail http://localhost:8080/health || exit 1