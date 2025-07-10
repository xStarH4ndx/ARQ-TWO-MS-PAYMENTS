# ---------- Etapa 1: Compilación ----------
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder

# Copia todo el proyecto y compila
WORKDIR /app
COPY pom.xml .
COPY src ./src

# Compila el proyecto y genera el .jar
RUN mvn clean package -DskipTests

# ---------- Etapa 2: Imagen final (producción) ----------
FROM eclipse-temurin:21-jdk-alpine

# Carpeta donde correrá la app
WORKDIR /app

# Copia el .jar desde la imagen builder
COPY --from=builder /app/target/ms-payments-0.0.1-SNAPSHOT.jar app.jar

# Puerto expuesto (ajústalo si usas otro)
EXPOSE 8083

# Variables opcionales para JVM (memoria, rendimiento, etc.)
ENV JAVA_OPTS="-XX:+UseContainerSupport -Dfile.encoding=UTF-8"

# Ejecuta el JAR
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
