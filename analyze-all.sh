#!/bin/bash
# Script d'analyse SonarQube complète (Backend + Frontend)
set -e  # Arrêt en cas d'erreur

# Charger les variables d'environnement depuis .env
if [ -f .env ]; then
    # shellcheck disable=SC2046
    export $(cat .env | grep -v '^#' | grep -v '^$' | xargs)
fi

echo "🚀 Analyse Complète SonarQube + JaCoCo"
echo ""
echo "Ce script va analyser:"
echo "  - Backend (Spring Boot + JaCoCo)"
echo "  - Frontend (Angular + Karma Coverage)"
echo ""

SONAR_URL="${SONAR_HOST_URL:-http://localhost:9000}"
echo "🔍 Vérification de SonarQube ($SONAR_URL)..."
if curl -s $SONAR_URL/api/system/status | grep -q "UP"; then
    echo "SonarQube est accessible"
else
    echo "Erreur: SonarQube n'est pas accessible sur $SONAR_URL"
    echo "Lancez: docker-compose -f docker-compose.dev.yml up -d sonarqube"
    exit 1
fi

echo ""
echo "1/2 - Analyse Backend..."
echo ""
./analyze-backend.sh

echo ""
echo "2/2 - Analyse Frontend..."
echo ""
./analyze-frontend.sh

echo ""
echo "Analyse complète terminée!"
echo ""
echo "Consultez les résultats:"
echo "   Backend:  ${SONAR_HOST_URL:-http://localhost:9000}/dashboard?id=air-sphere-connect-backend"
echo "   Frontend: ${SONAR_HOST_URL:-http://localhost:9000}/dashboard?id=air-sphere-connect-frontend"
echo ""
