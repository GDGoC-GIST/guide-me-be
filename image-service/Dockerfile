FROM openjdk:17-slim

WORKDIR /app

COPY ./*.jar app.jar

COPY ./resources ./resources

ARG SPRING_PROFILES_ACTIVE
ARG PORT

ENV SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE}
ENV PORT=${PORT}

EXPOSE ${PORT}

ENTRYPOINT ["java", "-jar", "app.jar"]