# 🌍 AirSphere Connect

Plateforme interactive pour les indicateurs environnementaux et forum écologique en région Occitanie.

## 👥 Équipe de développement
- **Sandrine ALCAZAR**
- **Cyril SCHNEIDENBACH**
- **Nuno ESTEVES**

## 🚀 Installation rapide

### Prérequis
- ✅ Docker Desktop installé et démarré
- ✅ Git installé
- ✅ HeidiSQL (Windows) ou MySQL Workbench (Linux)

### Installation
```bash
# 1. Cloner le projet
git clone https://github.com/AirSphereConnect/AirSphereConnect.git
cd AirSphereConnect

# 2. Configurer les variables d'environnement
cp .env.example .env
# Modifier .env si nécessaire

# 3. Donner les permissions (Linux/Mac seulement)
chmod +x scripts/*.sh

# 4. Démarrer la base de données
./scripts/start.sh
# ou directement : docker-compose up -d mariadb