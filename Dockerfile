# Stage 1: Build with Maven
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run with Tomcat
FROM tomcat:9.0-jdk21-openjdk-slim

# Install LibreOffice and dependencies
RUN apt-get update && apt-get install -y \
    libreoffice \
    libreoffice-java-common \
    fonts-dejavu \
    fonts-liberation \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Set environment variables
ENV SOFFICE_PATH=/usr/bin/soffice
ENV STORAGE_BASE_DIR=/app/data/uploads
# Limiter la mémoire de la JVM pour laisser de la place à LibreOffice (Plan Free 512MB)
ENV CATALINA_OPTS="-Xms128M -Xmx256M -XX:MaxMetaspaceSize=96M"

# Create data directory
RUN mkdir -p /app/data/uploads && chmod -R 777 /app/data

# Remove default webapps and copy our WAR
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /app/target/ConvertisseurDOC.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080

# Script pour adapter le port de Tomcat à la variable d'environnement $PORT de Render
CMD ["sh", "-c", "sed -i \"s/8080/${PORT:-8080}/g\" /usr/local/tomcat/conf/server.xml && catalina.sh run"]
