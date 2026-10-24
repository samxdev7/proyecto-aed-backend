# Etapa 1: Build (Maven 3.9.11 + JDK 21)
FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /app

# Copiamos solo el pom.xml primero para cachear las dependencias
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Luego el código fuente (esta capa cambia más seguido)
COPY src ./src
RUN mvn -B clean package -DskipTests

# Etapa 2: Runtime (solo JRE 21)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Usuario sin privilegios (no ejecutar como root)
RUN addgroup -S spring && adduser -S spring -G spring

COPY --from=build /app/target/*.jar app.jar
RUN chown spring:spring app.jar

USER spring:spring

# Informativo: Render inyecta PORT en tiempo de ejecución
EXPOSE 8080

# MaxRAMPercentage ajusta el heap al límite de memoria del contenedor
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]