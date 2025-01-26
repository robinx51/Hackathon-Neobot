FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY target/api-service-1.0-SNAPSHOT-exec.jar app.jar

EXPOSE 9090

ENTRYPOINT ["java", "-jar", "app.jar"]