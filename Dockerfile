# Use official Java 17 image
FROM eclipse-temurin:17-jdk

# Set working directory
WORKDIR /app

# Copy files
COPY . .

# Build the app
RUN ./mvnw clean package -DskipTests || mvn clean package -DskipTests

# Expose port (Render uses PORT env var)
EXPOSE 8080

# Run Spring Boot app
CMD ["sh", "-c", "java -jar target/*.jar"]
