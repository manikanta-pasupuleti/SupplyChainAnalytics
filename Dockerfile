FROM eclipse-temurin:17-jre

WORKDIR /app

COPY target/supplychain-analytics-1.0-SNAPSHOT.jar app.jar
COPY data ./data

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]