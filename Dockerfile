# Design-patterns starter: a plain Java 21 service built with the Gradle wrapper.
# No framework, so the runnable artifact is the regular jar (build/libs/app.jar).

# ---- build ------------------------------------------------------------------
FROM eclipse-temurin:21-jdk AS build
WORKDIR /src
COPY gradlew settings.gradle.kts build.gradle.kts ./
COPY gradle ./gradle
RUN chmod +x gradlew && ./gradlew --no-daemon dependencies > /dev/null
COPY src ./src
RUN ./gradlew --no-daemon jar

# ---- runtime ----------------------------------------------------------------
FROM eclipse-temurin:21-jre AS runtime
RUN useradd --uid 10001 --no-create-home app
WORKDIR /app
COPY --from=build /src/build/libs/app.jar app.jar
USER app
ENV PORT=8080 \
    JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75"
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
