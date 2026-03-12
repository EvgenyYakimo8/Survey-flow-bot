# ---- стадия сборки ----
FROM maven:3.9-eclipse-temurin-25 AS builder
WORKDIR /build
COPY pom.xml .
# Загружаем зависимости отдельно (кэширование)
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# ---- стадия запуска ----
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
# Копируем собранный JAR (обратите внимание на имя)
COPY --from=builder /build/target/Survey-Flow-Bot-0.0.1.jar app.jar

# Создаём непривилегированного пользователя
RUN addgroup -S app && adduser -S -G app app
USER app

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]