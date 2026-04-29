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

COPY --from=build --chown=spring:spring /workspace/build/libs/*.jar /app/app.jar

USER spring

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
