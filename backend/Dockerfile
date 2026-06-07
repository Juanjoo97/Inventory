# ---- Build stage ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q dependency:go-offline
COPY src ./src
RUN mvn -q clean package -DskipTests

# ---- Runtime stage ----
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/inventory-service-1.0.0.jar app.jar
# En el tier free (512 MB) la JVM, por ser container-aware, toma ~25% del heap
# por defecto y se queda corta. Subimos al 70% para usar la RAM disponible
# sin reventar. Se puede sobreescribir con JAVA_TOOL_OPTIONS desde Render.
ENV JAVA_OPTS="-XX:MaxRAMPercentage=70.0 -XX:+UseSerialGC"
EXPOSE 8080
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.jar"]