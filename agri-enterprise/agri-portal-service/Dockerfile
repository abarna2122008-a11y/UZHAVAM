FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
COPY api-gateway/pom.xml api-gateway/pom.xml
COPY agri-portal-service/pom.xml agri-portal-service/pom.xml
COPY agri-advisory-service/pom.xml agri-advisory-service/pom.xml
COPY farmer-workspace-service/pom.xml farmer-workspace-service/pom.xml
RUN mvn -B -pl agri-portal-service -am dependency:go-offline
COPY agri-portal-service/src agri-portal-service/src
RUN mvn -B -pl agri-portal-service -am package -DskipTests

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
RUN useradd --system --uid 10001 --create-home appuser
COPY --from=build /workspace/agri-portal-service/target/agri-portal-service-1.0.0.jar app.jar
USER 10001
EXPOSE 8081
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-XX:+UseContainerSupport", "-jar", "/app/app.jar"]
