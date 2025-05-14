FROM openjdk:17-slim
ENV TZ=Asia/Seoul
RUN apt-get update && apt-get install -y tzdata

WORKDIR /app

COPY --from=builder /build/build/libs/*.jar app.jar

ARG SPRING_PROFILES_ACTIVE
ARG PORT

ENV SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE}
ENV PORT=${PORT}

ENV OIDC_CLIENT_SECRET=""
ENV JWT=""
ENV ISSUER=""

EXPOSE ${PORT}

ENTRYPOINT ["java", "-jar", "app.jar"]