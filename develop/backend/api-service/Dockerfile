FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY target/api-service-1.0-SNAPSHOT-exec.jar app.jar

ENV STORAGE_SERVERURL=http://data:8081

EXPOSE 9093

ENTRYPOINT ["java", "-jar", "app.jar"]