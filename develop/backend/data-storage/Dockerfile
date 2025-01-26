# Используем базовый образ с Java (например, OpenJDK 17)
FROM eclipse-temurin:21-jdk-alpine

# Устанавливаем рабочую директорию внутри контейнера
WORKDIR /app

# Копируем JAR-файл приложения в контейнер
ARG JAR_FILE
COPY ${JAR_FILE} app.jar

# Копируем корневой сертификат для подключения к БД
COPY src/main/resources/db/root.crt /app/db/root.crt

# Устанавливаем переменную окружения для подключения к БД
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://rc1b-gk21kywyncnoym7q.mdb.yandexcloud.net:6432/cnrprod1737640949-team-83206?ssl=true&sslmode=verify-full&sslrootcert=/app/db/root.crt

# Открываем порт, на котором будет работать приложение
EXPOSE 8081

# Команда для запуска приложения
ENTRYPOINT ["java", "-jar", "app.jar"]
