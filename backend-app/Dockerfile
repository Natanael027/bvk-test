# Use a base image with Amazon Corretto
FROM amazoncorretto:21

# Set the working directory
WORKDIR /app

# Copy the jar file into the container
COPY build/libs/*.jar /app/app.jar

# Copy the configuration files
COPY src/main/resources/application.properties /app/application.properties

# Expose the port the app runs on
EXPOSE 8082

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
