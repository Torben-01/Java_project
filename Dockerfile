# ---------- Build: Java 21 mit Maven Wrapper, Vaadin baut das Frontend (lädt Node.js selbst) ----------
FROM eclipse-temurin:21-jdk AS build
WORKDIR /build

# Abhängigkeiten zuerst laden, damit sie bei Codeänderungen im Docker-Cache bleiben
COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw -B -q dependency:go-offline

COPY package.json package-lock.json tsconfig.json types.d.ts ./
COPY src src
RUN ./mvnw -B -DskipTests package && cp target/torben-photography-*.jar app.jar

# ---------- Laufzeit: nur die Java-Laufzeit und die gebaute App ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

# curl für den Health-Check, eigener Benutzer statt root
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* \
    && useradd --system --uid 10001 --home /app photography \
    && mkdir -p /app/bilder \
    && chown -R photography /app

COPY --from=build /build/app.jar /app/app.jar
USER photography

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75" \
    IMAGES_DIR=/app/bilder
EXPOSE 8080

HEALTHCHECK --interval=15s --timeout=5s --start-period=90s --retries=5 \
    CMD curl -fs http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
