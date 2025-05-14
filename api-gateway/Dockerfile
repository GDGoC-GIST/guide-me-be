FROM openjdk:17-slim

ARG JAR_FILE=build/libs/api-gateway-*.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8000

ENTRYPOINT ["java", "-jar", "/app.jar"]