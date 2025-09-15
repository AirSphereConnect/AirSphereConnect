#!/bin/bash

# 🚀 Script de démarrage AirSphere Connect
# Usage: ./start.sh

echo "🚀 Démarrage d'AirSphere Connect..."

# Arrêter les services existants (au cas où)
echo "🛑 Arrêt des services existants..."
docker-compose down

# Démarrer tous les services
echo "🐳 Démarrage des services Docker..."
docker-compose up -d

# Attendre un peu que tout démarre
echo "⏳ Attente du démarrage (30 secondes)..."
sleep 30

# Afficher le statut
echo "📊 Statut des services :"
docker-compose ps

echo ""
echo "✅ AirSphere Connect démarré !"
echo ""
echo "🔗 URLs disponibles :"
echo "   📊 Base de données : localhost:3306"
echo "   🌐 Backend API : http://localhost:8080"
echo "   🎨 Frontend : http://localhost:4200"
echo "   🔧 Adminer (DB) : http://localhost:8081"
echo ""
echo "💡 Commandes utiles :"
echo "   Logs : docker-compose logs -f"
echo "   Arrêt : docker-compose down"
echo "   Base : docker-compose exec database mysql -u airsphere_user -p"