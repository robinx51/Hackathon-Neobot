# Используем базовый образ с Java (например, OpenJDK 17)
FROM eclipse-temurin:21-jdk-alpine

# Устанавливаем рабочую директорию внутри контейнера
WORKDIR /app

# Копируем JAR-файл приложения в контейнер
COPY target/data-storage-1.0-SNAPSHOT-exec.jar app.jar
COPY src/main/resources/db/root.crt /app/db/root.crt
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://rc1b-gk21kywyncnoym7q.mdb.yandexcloud.net:6432/cnrprod1737640949-team-83206?ssl=true&sslmode=verify-full&sslrootcert=/app/db/root.crt

# Открываем порт, на котором будет работать приложение
EXPOSE 8081

# Команда для запуска приложения
ENTRYPOINT ["java", "-jar", "app.jar"]