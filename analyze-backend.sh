#!/bin/bash

# ==============================
# Script d'analyse SonarQube + JaCoCo - Backend
# ==============================

set -e  # Arrêt en cas d'erreur

# Charger les variables d'environnement depuis .env
if [ -f .env ]; then
    export $(cat .env | grep -v '^#' | grep -v '^$' | xargs)
fi

echo "========================================="
echo "🔍 Analyse Backend (Spring Boot)"
echo "========================================="
echo ""

# Variables
SONAR_HOST="${SONAR_HOST_URL:-http://localhost:9000}"
SONAR_TOKEN="${SONAR_TOKEN:-YOUR_SONARQUBE_TOKEN_HERE}"

cd air-sphere-connect-back

echo "📦 Nettoyage et compilation..."
./mvnw clean

echo "✅ Exécution des tests + coverage JaCoCo..."
./mvnw verify

echo "📊 Envoi des résultats vers SonarQube..."
# Note: Override du projectKey Maven avec celui défini dans sonar-project.properties
./mvnw sonar:sonar \
  -Dsonar.projectKey=air-sphere-connect \
  -Dsonar.projectName="AirSphere Connect - Backend" \
  -Dsonar.host.url="$SONAR_HOST" \
  -Dsonar.token="$SONAR_TOKEN"

echo ""
echo "✅ Analyse backend terminée!"
echo "🌐 Résultats: $SONAR_HOST/dashboard?id=air-sphere-connect"
echo ""
