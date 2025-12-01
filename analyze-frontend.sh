#!/bin/bash

set -e
set -x  # debug

# Charger les variables d'environnement
if [ -f .env ]; then
    set -a
    source .env
    set +a
fi

echo "========================================="
echo "🔍 Analyse Frontend (Angular)"
echo "========================================="

SONAR_TOKEN="${SONAR_TOKEN:-YOUR_SONARQUBE_TOKEN_HERE}"

cd air-sphere-connect-front || { echo "❌ Dossier introuvable"; exit 1; }

echo "📦 Installation des dépendances (si nécessaire)..."
npm install --legacy-peer-deps

echo "✅ Exécution des tests + coverage..."
npm run test:coverage || echo "⚠️ Tests échoués mais on continue l'analyse..."

echo "📊 Envoi des résultats vers SonarQube..."
npx sonar-scanner -Dsonar.token="$SONAR_TOKEN"

echo ""
echo "✅ Analyse frontend terminée!"
echo "🌐 Résultats: http://localhost:9000/dashboard?id=air-sphere-connect-frontend"
echo ""
