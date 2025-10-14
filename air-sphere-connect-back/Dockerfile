# Build stage
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copier les fichiers Maven
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .


# Rendre mvnw exécutable et corriger CRLF → LF
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw

# Télécharger les dépendances (cache Docker)
RUN ./mvnw dependency:go-offline -B

# Copier le code source
COPY src src

# Build de l'application
RUN ./mvnw clean package -DskipTests -e -X


# Production stage
FROM eclipse-temurin:21-jre-alpine

# ✅ Installer curl pour health check
RUN apk add --no-cache curl

WORKDIR /app

# Copier le JAR depuis le builder
COPY --from=builder /app/target/*.jar app.jar

# Exposer les ports
EXPOSE 8080
EXPOSE 5005

# Lancer l'application
#ENTRYPOINT ["java", "-jar", "/app/app.jar"]
ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005", "-jar", "/app/app.jar"]
