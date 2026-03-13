# Use Eclipse Temurin with Maven for the build stage.
# https://hub.docker.com/_/maven
FROM maven:3.9-eclipse-temurin-11 AS build-env

# Set the working directory to /app
WORKDIR /app
# Copy the pom.xml file to download dependencies
COPY pom.xml ./
# Copy local code to the container image.
COPY src ./src

# Download dependencies and build a release artifact.
RUN mvn package -DskipTests

# Use Eclipse Temurin JRE for the runtime image.
# https://hub.docker.com/_/eclipse-temurin
FROM eclipse-temurin:11-jre

# Copy the jar to the production image from the builder stage.
COPY --from=build-env /app/target/home-automation*.jar /home-automation.jar

# Run the web service on container startup.
CMD ["java", "-jar", "/home-automation.jar"]
