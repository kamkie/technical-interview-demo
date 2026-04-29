# syntax=docker/dockerfile:1.7

FROM eclipse-temurin:25-jdk-noble AS build

WORKDIR /workspace

COPY gradlew build.gradle.kts settings.gradle.kts ./
COPY gradle gradle

RUN chmod +x gradlew
RUN GIT_VERSION=0.0.0 ./gradlew --no-daemon dependencies

COPY .git/ ./.git/
COPY src src

RUN ./gradlew --no-daemon bootJar

FROM eclipse-temurin:25-jre-noble

WORKDIR /app

RUN useradd --system --create-home --home-dir /app --shell /usr/sbin/nologin spring

ENV JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport -XX:InitialRAMPercentage=25.0 -XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError -Djava.security.egd=file:/dev/./urandom"

COPY --from=build --chown=spring:spring /workspace/build/libs/*.jar /app/app.jar

USER spring

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
    CMD bash -ec 'exec 3<>/dev/tcp/127.0.0.1/8080; printf "GET /actuator/health/readiness HTTP/1.1\r\nHost: localhost\r\nConnection: close\r\n\r\n" >&3; grep "200" <&3 >/dev/null'

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
