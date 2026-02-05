# Use an official Java runtime as the base
# Think of this as the foundation - like choosing what type of box to pack your gift in
# Using Eclipse Temurin (the modern replacement for OpenJDK)
FROM eclipse-temurin:11-jre

# Set the working directory inside the container
# This is like organizing items in a specific folder inside the box
WORKDIR /app

# Copy the compiled JAR file from your target folder into the container
# This is copying your actual application into the box
COPY target/maven-jenkins-project-1.0-SNAPSHOT.jar /app/app.jar

# Tell Docker what command to run when the container starts
# This is like adding "press this button to start" instructions
ENTRYPOINT ["java", "-jar", "app.jar"]
