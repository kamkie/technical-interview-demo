# syntax=docker/dockerfile:1.7

FROM mcr.microsoft.com/openjdk/jdk:25-ubuntu

WORKDIR /home/app

ENV JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport -XX:InitialRAMPercentage=25.0 -XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError -Djava.security.egd=file:/dev/./urandom"
ENV SPRING_PROFILES_ACTIVE=prod

ARG JAR_FILE=build/libs/technical-interview-demo-*-boot.jar
COPY --chown=app:app ${JAR_FILE} app.jar

USER app

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
    CMD bash -ec 'exec 3<>/dev/tcp/127.0.0.1/8080; printf "GET /actuator/health/readiness HTTP/1.1\r\nHost: localhost\r\nConnection: close\r\n\r\n" >&3; grep "200" <&3 >/dev/null'

ENTRYPOINT ["jaz", "-jar", "/home/app/app.jar"]
