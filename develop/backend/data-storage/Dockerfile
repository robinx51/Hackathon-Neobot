FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY target/data-storage-1.0-SNAPSHOT-exec.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]