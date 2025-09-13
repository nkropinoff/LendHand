FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline

COPY src ./src

RUN mvn package -DskipTests

FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY --from=builder /app/target/lendhand-service-0.0.1-SNAPSHOT.jar .

# Указываем порт
EXPOSE 8080

CMD ["java", "-jar", "lendhand-service-0.0.1-SNAPSHOT.jar"]
