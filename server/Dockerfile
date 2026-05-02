FROM node:24-alpine AS frontend-stage
WORKDIR /frontend

COPY src/main/frontend/package*.json .
RUN npm ci --ignore-scripts

COPY src/main/frontend/main.css src/main/frontend/main.css
COPY src/main/resources/templates src/main/resources/templates
RUN npx tailwindcss -i src/main/frontend/main.css -o main.min.css --minify

FROM eclipse-temurin:21-jdk-alpine AS java-builder
WORKDIR /app

COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN ./mvnw -B -q dependency:go-offline

COPY src src
COPY --from=frontend-stage /frontend/main.min.css ./src/main/resources/static/css/main.css
RUN ./mvnw -B -q clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S spring && adduser -S spring-app -G spring
USER spring-app:spring

COPY --from=java-builder /app/target/*.jar app.jar

ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]