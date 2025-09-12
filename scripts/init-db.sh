#!/bin/bash
# Arrête et supprime les containers
docker-compose down

# Supprime le volume de la base de données si existant
docker volume rm air-sphere-connect_db_data 2>/dev/null

# Démarre seulement MariaDB
docker-compose up -d mariadb

echo "Base de données MariaDB démarrée et prête à l'emploi."
