# ============================================================================
# Multi-stage build for the OpenFGA wrapper service.
#   Stage 1 (build) compiles and packages the app with Maven.
#   Stage 2 (runtime) ships only the JRE + fat JAR for a small, secure image.
# ============================================================================

# ---- Stage 1: build ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace

# Cache dependencies first for faster incremental builds.
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Compile and package (tests run in CI, skipped here to keep image builds fast).
COPY src ./src
RUN mvn -B clean package -DskipTests

# ---- Stage 2: runtime ----
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Run as an unprivileged user.
RUN groupadd --system spring && useradd --system --gid spring spring
USER spring:spring

COPY --from=build /workspace/target/openfga-wrapper-*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
