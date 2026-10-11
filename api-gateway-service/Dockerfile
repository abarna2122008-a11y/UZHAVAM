FROM node:22-alpine AS frontend
WORKDIR /workspace/api-gateway-service/frontend
COPY api-gateway-service/frontend/package.json api-gateway-service/frontend/package-lock.json ./
RUN npm ci
COPY api-gateway-service/frontend/ ./
RUN npm run build

FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
COPY api-gateway-service/pom.xml api-gateway-service/pom.xml
COPY agri-portal-service/pom.xml agri-portal-service/pom.xml
COPY agri-advisory-service/pom.xml agri-advisory-service/pom.xml
COPY api-gateway-service/src api-gateway-service/src
COPY --from=frontend /workspace/api-gateway-service/src/main/resources/static/assets/plant-health.js api-gateway-service/src/main/resources/static/assets/plant-health.js
RUN mvn -B -pl api-gateway-service -am package -DskipTests

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
RUN useradd --system --uid 10001 --create-home appuser
COPY --from=build /workspace/api-gateway-service/target/api-gateway-service-1.0.0.jar app.jar
USER 10001
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar"]
