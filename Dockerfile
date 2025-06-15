# Imagen base para Java 21
FROM eclipse-temurin:21-jdk-alpine

# Crear directorio para la app
WORKDIR /app

# Copiar el .jar generado al contenedor
# Asegúrate de que el nombre del JAR sea correcto.
# Si tu proyecto Maven/Gradle genera un JAR con un nombre diferente,
# por favor, ajusta 'ms-payments-0.0.1-SNAPSHOT.jar'
COPY target/ms-payments-0.0.1-SNAPSHOT.jar app.jar

# Exponer el puerto que usa tu aplicación
# Según tu application.properties, es el puerto 8083
EXPOSE 8083

# Comando para ejecutar la app
# El 'app.jar' es el nombre que le dimos al JAR copiado
ENTRYPOINT ["java", "-jar", "app.jar"]