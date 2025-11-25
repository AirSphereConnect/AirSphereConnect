#!/bin/bash

# ==============================
# Script d'analyse SonarQube + JaCoCo - Backend
# ==============================

set -e  # Arrêt en cas d'erreur

echo "========================================="
echo "🔍 Analyse Backend (Spring Boot)"
echo "========================================="
echo ""

# Variables
SONAR_HOST="${SONAR_HOST:-http://localhost:9000}"
SONAR_TOKEN="${SONAR_TOKEN:-YOUR_SONARQUBE_TOKEN_HERE}"
PROJECT_KEY="${PROJECT_KEY:-air-sphere-connect}"

cd air-sphere-connect-back

echo "📦 Nettoyage et compilation..."
./mvnw clean

echo "✅ Exécution des tests + coverage JaCoCo..."
./mvnw verify

echo "📊 Envoi des résultats vers SonarQube..."
./mvnw sonar:sonar \
  -Dsonar.projectKey="$PROJECT_KEY" \
  -Dsonar.host.url="$SONAR_HOST" \
  -Dsonar.token="$SONAR_TOKEN"

echo ""
echo "✅ Analyse backend terminée!"
echo "🌐 Résultats: $SONAR_HOST/dashboard?id=$PROJECT_KEY"
echo ""
