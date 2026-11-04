# Build the Spring Boot application on Render; no locally built JAR is required.
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /workspace
COPY . .
RUN chmod +x gradlew \
    && ./gradlew --no-daemon -Pprod bootJar -x test \
    && find build/libs -maxdepth 1 -type f -name '*.jar' ! -name '*-plain.jar' -exec cp {} /workspace/app.jar \; \
    && test -s /workspace/app.jar

FROM eclipse-temurin:21-jre-jammy AS runtime
WORKDIR /app
RUN groupadd --system --gid 10001 qualk \
    && useradd --system --uid 10001 --gid qualk --no-create-home qualk
COPY --from=build --chown=10001:10001 /workspace/app.jar /app/app.jar

ENV SPRING_PROFILES_ACTIVE=prod,render,supabase \
    SPRING_DOCKER_COMPOSE_ENABLED=false \
    JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=60 -XX:+ExitOnOutOfMemoryError"
USER 10001:10001
EXPOSE 10000
# Exec form lets Spring Boot receive SIGTERM for graceful shutdown.
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
