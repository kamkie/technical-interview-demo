# syntax=docker/dockerfile:1.7

FROM eclipse-temurin:25-jre-jammy@sha256:0df1bb22182727e325476c0a9ab38ec4d2b042cbce0ea18a7da71284fea0c40c

RUN groupadd --system --gid 10001 app \
    && useradd --system --uid 10001 --gid 10001 --create-home --home-dir /home/app --shell /usr/sbin/nologin app \
    && mkdir -p /opt/app \
    && chown -R app:app /opt/app /home/app

WORKDIR /opt/app

ENV JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport -XX:InitialRAMPercentage=25.0 -XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError -Djava.io.tmpdir=/tmp -Djava.security.egd=file:/dev/./urandom"
ENV SPRING_PROFILES_ACTIVE=prod

ARG JAR_FILE=build/libs/technical-interview-demo-*-boot.jar
COPY --chown=app:app ${JAR_FILE} app.jar

USER app

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
    CMD bash -ec 'exec 3<>/dev/tcp/127.0.0.1/8080; printf "GET /actuator/health/readiness HTTP/1.1\r\nHost: localhost\r\nConnection: close\r\n\r\n" >&3; grep "200" <&3 >/dev/null'

ENTRYPOINT ["java", "-jar", "/opt/app/app.jar"]
