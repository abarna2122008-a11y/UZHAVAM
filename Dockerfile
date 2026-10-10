FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
COPY api-gateway-service/pom.xml api-gateway-service/pom.xml
COPY agri-portal-service/pom.xml agri-portal-service/pom.xml
COPY agri-advisory-service/pom.xml agri-advisory-service/pom.xml
COPY agri-advisory-service/src agri-advisory-service/src
RUN mvn -B -pl agri-advisory-service -am package -DskipTests

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
RUN useradd --system --uid 10001 --create-home appuser
COPY --from=build /workspace/agri-advisory-service/target/agri-advisory-service-1.0.0.jar app.jar
USER 10001
EXPOSE 8082
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar"]
