#!/bin/bash

# ==============================
# Script d'analyse SonarQube + Coverage - Frontend
# ==============================

set -e  # Arrêt en cas d'erreur

# Charger les variables d'environnement depuis .env
if [ -f .env ]; then
    export $(cat .env | grep -v '^#' | grep -v '^$' | xargs)
fi

echo "========================================="
echo "🔍 Analyse Frontend (Angular)"
echo "========================================="
echo ""

# Variables
SONAR_TOKEN="${SONAR_TOKEN:-YOUR_SONARQUBE_TOKEN_HERE}"

cd air-sphere-connect-front

echo "📦 Installation des dépendances (si nécessaire)..."
npm install --silent

echo "✅ Exécution des tests + coverage..."
npm run test:coverage

echo "📊 Envoi des résultats vers SonarQube..."
npx sonar-scanner -Dsonar.token="$SONAR_TOKEN"

echo ""
echo "✅ Analyse frontend terminée!"
echo "🌐 Résultats: http://localhost:9000/dashboard?id=air-sphere-connect"
echo ""
