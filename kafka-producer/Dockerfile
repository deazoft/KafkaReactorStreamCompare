# Use a base image with Java (ensure the version matches your project)
FROM openjdk:11

# Set a working directory in the container
WORKDIR /app

# Copy the Gradle wrapper and build files into the container
COPY gradlew .
COPY gradle gradle
COPY app/build.gradle .
COPY settings.gradle .

# Copy the source code into the container
COPY app/src src

# Build the application (skip tests to speed up build in production)
RUN ./gradlew build -x test

# Define the command to run the application
CMD ["./gradlew", "run"]
