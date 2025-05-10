FROM maven:3.8-openjdk-17 AS build
WORKDIR /app

# D'abord copier uniquement le POM pour cacher les dépendances
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Puis copier le code source
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]