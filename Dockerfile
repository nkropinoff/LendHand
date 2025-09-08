FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw .
COPY pom.xml .

RUN ./mvnw dependency:go-offline

COPY src /.src

RUN ./mvnw package -DskipTests

EXPOSE 8080

CMD ["java", "-jar", "target/lendhand-service-0.0.1-SNAPSHOT.jar"]