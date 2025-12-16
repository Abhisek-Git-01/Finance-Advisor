# Maven + JDK 21 image
FROM maven:3.9.6-eclipse-temurin-21

WORKDIR /app

# Copy project files
COPY . .

# Build Spring Boot app
RUN mvn clean package -DskipTests

# Run the app
CMD ["java", "-jar", "target/*.jar"]
