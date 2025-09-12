#!/bin/bash
# Restart all background services
docker-compose up -d

echo "Environnement de développement démarré :"
echo "- Base de données MariaDB"
echo "- Backend Spring Boot"
echo "- Frontend Angular"
