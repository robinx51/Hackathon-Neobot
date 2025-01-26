# Используем базовый образ с Java (например, OpenJDK 17)
FROM eclipse-temurin:21-jdk-alpine

# Устанавливаем рабочую директорию внутри контейнера
WORKDIR /app

# Копируем JAR-файл приложения в контейнер
COPY target/data-storage-1.0-SNAPSHOT-exec.jar app.jar

# Открываем порт, на котором будет работать приложение
EXPOSE 8080

# Команда для запуска приложения
ENTRYPOINT ["java", "-jar", "app.jar"]