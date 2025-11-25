#!/bin/bash

# ==============================
# Script d'analyse SonarQube complète (Backend + Frontend)
# ==============================

set -e  # Arrêt en cas d'erreur

echo "============================================="
echo "🚀 Analyse Complète SonarQube + JaCoCo"
echo "============================================="
echo ""
echo "Ce script va analyser:"
echo "  - Backend (Spring Boot + JaCoCo)"
echo "  - Frontend (Angular + Karma Coverage)"
echo ""

# Vérifier que SonarQube est accessible
echo "🔍 Vérification de SonarQube..."
if curl -s http://localhost:9000/api/system/status | grep -q "UP"; then
    echo "✅ SonarQube est accessible"
else
    echo "❌ Erreur: SonarQube n'est pas accessible sur http://localhost:9000"
    echo "💡 Lancez: docker-compose -f docker-compose.dev.yml up -d sonarqube"
    exit 1
fi

echo ""
echo "========================================="
echo "1/2 - Analyse Backend..."
echo "========================================="
./analyze-backend.sh

echo ""
echo "========================================="
echo "2/2 - Analyse Frontend..."
echo "========================================="
./analyze-frontend.sh

echo ""
echo "============================================="
echo "✅ Analyse complète terminée!"
echo "============================================="
echo ""
echo "📊 Consultez les résultats:"
echo "   http://localhost:9000/dashboard?id=air-sphere-connect"
echo ""
