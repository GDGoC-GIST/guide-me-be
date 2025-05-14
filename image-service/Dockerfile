FROM gradle:8.5-jdk17 AS builder
WORKDIR /build
COPY . .
RUN gradle clean build -x test --no-daemon

FROM openjdk:17-slim
ENV TZ=Asia/Seoul
RUN apt-get update && apt-get install -y tzdata

WORKDIR /app

COPY --from=builder /build/build/libs/*.jar app.jar

COPY --from=builder /build/src/main/resources ./resources

ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 9000

ENTRYPOINT ["java", "-jar", "app.jar"]