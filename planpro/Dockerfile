# Use lightweight JDK image
FROM openjdk:17-jdk-slim 

# Set working directory
WORKDIR /app

# Copy built JAR file
COPY build/libs/*.jar app.jar

# Run the Spring Boot app
CMD ["java", "-jar", "app.jar"]
