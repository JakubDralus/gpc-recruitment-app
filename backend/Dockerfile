# Use the official openjdk image from the Docker Hub
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /GPC-APP

# Copy the Spring Boot jar file to the container
COPY target/*.jar app.jar

# Expose the port your application runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]


# Stage 1: Build the application
#FROM maven:3.8.5-openjdk-17 AS builder
#
## Set the working directory inside the container
#WORKDIR /GPC-APP
#
## Copy the pom.xml file and download dependencies
#COPY pom.xml .
#RUN mvn dependency:go-offline
#
## Copy the source code and package the application
#COPY src/ ./src/
#RUN mvn clean package -DskipTests=true
#
## Stage 2: Run the application
#FROM eclipse-temurin:17-jdk-alpine AS prod
#
## Create the directory for the application
#RUN mkdir /GPC-APP
#
## Copy the built jar file from the builder stage
#COPY --from=builder /GPC-APP/target/*.jar /GPC-APP/app.jar
#
## Set the working directory
#WORKDIR /GPC-APP
#
## Expose the port your application runs on
#EXPOSE 8080
#
## Run the application
#ENTRYPOINT ["java", "-jar", "app.jar"]
