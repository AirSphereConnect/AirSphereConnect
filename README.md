
<div align="center">

# 🌍 AirSphere Connect  

Plateforme interactive pour les indicateurs environnementaux et forum écologique en région Occitanie.

</div>

<br>

## 👥 Équipe de développement
- **Cyril SCHNEIDENBACH**
- **Nuno ESTEVES**
- **Sandrine ALCAZAR**

<br>

## 🐳 Guide de Développement Docker - AirSphere Connect
Ce guide explique comment mettre en place et utiliser l'environnement de développement dockerisé du projet AirSphere Connect.


### 📋 Table des matières

- [Prérequis](#-prérequis)
- [Architecture de l'environnement](#️-architecture-de-lenvironnement)
- [Installation](#-installation)
- [Démarrage rapide](#-démarrage-rapide)
- [Structure des fichiers](#-structure-des-fichiers)
- [Configuration](#️-configuration)
- [Commandes utiles](#️-commandes-utiles)
- [Volumes et persistance](#-volumes-et-persistance)
- [Hot Reload](#-hot-reload)
- [Débogage](#-débogage)
- [Troubleshooting](#-troubleshooting)
- [Bonnes pratiques](#-bonnes-pratiques)
- [Ressources](#-ressources)

<br>

## 🔧 Prérequis
- **Docker** : Version 24.0+ ([Télécharger](https://www.docker.com/products/docker-desktop))
- **Docker Compose** : Version 2.0+ (inclus avec Docker Desktop)
- **Git** : Pour cloner le projet
- **IDE recommandé** :
  -  IntelliJ IDEA (Backend Java)
  -  Web Storm ou Visual Studio Code (Frontend Angular)
- **Système d'exploitation** : Windows 10/11, macOS, ou Linux

###  Vérification de l'installation
```bash
docker --version
docker-compose --version
mariadb --version
git --version
```
<br>

## 🏗️ Architecture de l'environnement
L'environnement de développement est composé des services suivants :
```
┌─────────────────────────────────────────────────────────┐
│                    Docker Network                       │
│                 air-sphere-connect-network              │
│                                                         │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐   │
│  │   MariaDB    │  │   Backend    │  │   Frontend   │   │
│  │  (Port 3306) │◄─│  (Port 8080) │◄─│  (Port 4200) │   │
│  └──────────────┘  └──────────────┘  └──────────────┘   │
│         ▲                                               │
│         │          ┌──────────────┐                     │
│         └──────────│   MailHog    │                     │
│                    │ (Port 1025)  │                     │
│                    │ (Port 8025)  │                     │
│                    └──────────────┘                     │
└─────────────────────────────────────────────────────────┘
```

### Services  

| Service | Image | Rôle | Port(s) |
|---------|-------|------|---------|
| **MariaDB** | `mariadb:12.0.2` | Base de données | `3306` |
| **Backend** | Custom (Spring Boot) | API REST | `8080` (HTTP), `5005` (Debug) |
| **Frontend** | Custom (Angular) | Interface utilisateur | `4200` |
| **MailHog** | `mailhog/mailhog` | Serveur mail de test | `1025` (SMTP), `8025` (Web UI) |

<br>

## 📦 Installation
1. **Cloner le projet**
   ```bash
   git clone https://github.com/votre-org/air-sphere-connect.git
   cd air-sphere-connect
   ```
2. **Créer le fichier `.env`**
   ```bash
   cp .env.example .env
   ```
3. **Configurer les variables d'environnement**  
   Éditez le fichier `.env` et ajustez les valeurs si nécessaire :
   ```bash
   # Base de données
   DB_ROOT_PASSWORD=${DB_ROOT_PASSWORD}
   DB_NAME=${DB_NAME}
   DB_USER=${DB_USER}
   DB_PASSWORD=${DB_PASSWORD}

   # APIs externes (optionnel pour commencer)
   WEATHER_API_KEY=${WEATHER_API_KEY}
   MAILJET_API_KEY=${MAILJET_API_KEY}
   MAILJET_API_SECRET=${MAILJET_API_SECRET}
   ```
4. **Créer les dossiers nécessaires**
   ```bash
   mkdir -p config/database
   ```
<br>

## 🚀 Démarrage rapide
### Première utilisation
```bash
# 1. Build des images Docker
docker-compose -f docker-compose.dev.yml build

# 2. Démarrer tous les services
docker-compose -f docker-compose.dev.yml up
```
⏱️ Temps de démarrage : Environ 2-3 minutes la première fois (téléchargement des dépendances Maven et npm).

### Vérifications
Après 2-3 minutes, vérifiez que tout fonctionne :
```bash
# État des conteneurs
docker-compose -f docker-compose.dev.yml ps

# Résultat attendu :
# NAME                    STATUS
# air-sphere-connect_db   Up (healthy)
# mailhog                 Up
# airsphere_backend       Up
# airsphere_frontend      Up
```

### Accéder aux services

| Service | URL | Identifiants |
|---------|-----|--------------|
| **Frontend** | http://localhost:4200 | - |
| **Backend API** | http://localhost:8080 | - |
| **Backend Health** | http://localhost:8080/actuator/health | - |
| **MailHog UI** | http://localhost:8025 | - |

<br>

## 📁 Structure des fichiers
```
air-sphere-connect/
├── docker-compose.dev.yml          # Configuration Docker Compose
├── .env                            # Variables d'environnement (non versionné)
├── .env.example                    # Template des variables
├── .gitignore                      # Fichiers ignorés par Git
│
├── config/                         # Configuration des services
│   └── database/                   
│       └── 02-mock-data.sql        # Données de test (exécuté au 1er démarrage)
│
├── air-sphere-connect-back/        # Backend Spring Boot
│   ├── Dockerfile.dev              # Image Docker de développement
│   ├── pom.xml                     # Dépendances Maven
│   └── src/                        # Code source (hot reload actif)
│
└── air-sphere-connect-front/       # Frontend Angular
├── Dockerfile.dev                  # Image Docker de développement
├── package.json                    # Dépendances npm
└── src/                            # Code source (hot reload actif)
```
<br>

## ⚙️ Configuration
**Variables d'environnement (`.env`)**  
Les variables d'environnement sont chargées depuis le fichier .env :
```bash
# Base de données
MARIADB_VERSION=${MARIADB_VERSION}
DB_CONTAINER_NAME=${DB_CONTAINER_NAME}
DB_HOST=${DB_HOST}
DB_PORT=${DB_PORT}
DB_NAME=${DB_NAME}
DB_USER=${DB_USER}
DB_PASSWORD=${DB_PASSWORD}
DB_ROOT_PASSWORD=${DB_ROOT_PASSWORD}
DB_CHARSET=${DB_CHARSET}
DB_COLLATION=${DB_COLLATION}
DB_VOLUME_NAME=${DB_VOLUME_NAME}

# Backend
BACKEND_CONTEXT=${BACKEND_CONTEXT}
BACKEND_DOCKERFILE=${BACKEND_DOCKERFILE}
BACKEND_CONTAINER_NAME=${BACKEND_CONTAINER_NAME}
BACKEND_PORT=${BACKEND_PORT}
DEBUG_PORT=${DEBUG_PORT}

# Frontend
FRONTEND_CONTEXT=${FRONTEND_CONTEXT}
FRONTEND_DOCKERFILE=${FRONTEND_DOCKERFILE}
FRONTEND_CONTAINER_NAME=${FRONTEND_CONTAINER_NAME}
FRONTEND_PORT=${FRONTEND_PORT}

# Spring Boot
SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE}
JPA_DDL_AUTO=${JPA_DDL_AUTO}
JPA_SHOW_SQL=${JPA_SHOW_SQL}

# Mail (MailHog)
MAIL_HOST=${MAIL_HOST}
MAIL_PORT=${MAIL_PORT}

# APIs externes (optionnel)
WEATHER_API_KEY=${WEATHER_API_KEY}
MAILJET_API_KEY=${MAILJET_API_KEY}
MAILJET_API_SECRET=${MAILJET_API_SECRET}

# CORS
CORS_ORIGINS=${CORS_ORIGINS}

# Network
NETWORK_NAME=${NETWORK_NAME}
```

### Profils Spring Boot
Le backend utilise le profil `dev` en environnement Docker :

- JPA : `ddl-auto: update` (crée/met à jour les tables automatiquement)
- SQL : `show-sql: true` (affiche les requêtes dans les logs)
- Scheduler : Désactivé (`SPRING_SCHEDULING_ENABLED=false`)
- Sécurité : Désactivée (profil `@Profile("dev")`)

<br>


## 🛠️ Commandes utiles
### Gestion des conteneurs
```bash
# Démarrer tous les services (mode foreground)
docker-compose -f docker-compose.dev.yml up

# Démarrer en arrière-plan (mode detached)
docker-compose -f docker-compose.dev.yml up -d

# Arrêter tous les services
docker-compose -f docker-compose.dev.yml down

# Arrêter ET supprimer les volumes (⚠️ PERTE DE DONNÉES)
docker-compose -f docker-compose.dev.yml down -v

# Redémarrer un service spécifique
docker-compose -f docker-compose.dev.yml restart backend

# Voir l'état des services
docker-compose -f docker-compose.dev.yml ps
```
### Logs  

```bash
# Voir tous les logs
docker-compose -f docker-compose.dev.yml logs

# Suivre les logs en temps réel
docker-compose -f docker-compose.dev.yml logs -f

# Logs d'un service spécifique
docker-compose -f docker-compose.dev.yml logs backend
docker-compose -f docker-compose.dev.yml logs -f frontend

# Dernières 50 lignes
docker-compose -f docker-compose.dev.yml logs --tail=50 backend
Rebuild
bash# Rebuild toutes les images
docker-compose -f docker-compose.dev.yml build

# Rebuild sans cache (force le téléchargement)
docker-compose -f docker-compose.dev.yml build --no-cache

# Rebuild un service spécifique
docker-compose -f docker-compose.dev.yml build backend

# Rebuild ET redémarrer
docker-compose -f docker-compose.dev.yml up --build
```
### Accès aux conteneurs
```bash
# Shell dans le conteneur backend
docker exec -it airsphere_backend sh

# Shell dans le conteneur MariaDB
docker exec -it air-sphere-connect_db sh

# Connexion MySQL directe
docker exec -it air-sphere-connect_db mariadb -u root -prootpass123

# Exécuter une commande Maven dans le backend
docker exec -it airsphere_backend mvn clean install
```
<br>


## 💾 Volumes et persistance
### Volumes nommés
Le projet utilise 3 volumes Docker pour la persistance :

| Volume | Contenu | Persistance |
|---------|-----|--------------|
| **air-sphere-connect_db_data** | Données MariaDB | ✅ Conservé |
| **maven_cache** | tp:// | ✅ Conservé |
| **node_modules** | Dépendances npm | ✅ Conservé |

### Avantages des volumes
✅ Persistance : Les données survivent aux redémarrages  
✅ Performance : Cache des dépendances (pas de re-téléchargement)  
✅ Isolation : Séparation entre code et données

### Gestion des volumes
```bash
# Lister les volumes
docker volume ls

# Inspecter un volume
docker volume inspect air-sphere-connect_db_data

# Supprimer un volume spécifique (⚠️ PERTE DE DONNÉES)
docker volume rm air-sphere-connect_db_data

# Supprimer tous les volumes non utilisés
docker volume prune
```
#### Reset complet
Pour repartir de zéro :
```bash
# 1. Arrêter et supprimer tout
docker-compose -f docker-compose.dev.yml down -v

# 2. Nettoyer le système Docker
docker system prune -f

# 3. Redémarrer
docker-compose -f docker-compose.dev.yml up --build
```
<br>


## 🔥 Hot Reload
Le hot reload permet de voir vos modifications instantanément sans redémarrer les conteneurs.

### Backend (Spring Boot)
#### Fichiers synchronisés :
```yaml
volumes:
- ./air-sphere-connect-back/src:/app/src:ro      # Code source
- ./air-sphere-connect-back/pom.xml:/app/pom.xml:ro  # Config Maven
  ```
  **Comment ça marche ?**

- Spring Boot Devtools détecte les changements dans `src/`
- Recompilation automatique
- Rechargement du contexte Spring

Délai : ~2-5 secondes après la sauvegarde  

**⚠️ Limitations :**

**Modifications du pom.xml → Redémarrer le conteneur :**

```bash  
docker-compose -f docker-compose.dev.yml restart backend
```
<br>


### Frontend (Angular)
#### Fichiers synchronisés :
```yaml
volumes:
- ./air-sphere-connect-front/src:/app/src:ro
- ./air-sphere-connect-front/angular.json:/app/angular.json:ro
- ./air-sphere-connect-front/package.json:/app/package.json:ro
 ```
  **Comment ça marche ?**

Angular CLI (ng serve) surveille les fichiers
Recompilation automatique
Rechargement du navigateur (live reload)

Délai : ~1-3 secondes après la sauvegarde  

**⚠️ Limitations :**

**Modifications du package.json → Redémarrer le conteneur :**

```bash  
docker-compose -f docker-compose.dev.yml restart frontend
```

<br>


## 🐛 Débogage
### Backend (Java)
Le port 5005 est exposé pour le débogage distant.

#### IntelliJ IDEA

1. Run → Edit Configurations...
2. Add New Configuration → Remote JVM Debug
3. Configuration :
   - Host : `localhost`
   - Port : `5005` 
   - Use module classpath : Sélectionner le module backend
4. Apply → OK
5. Placer des breakpoints dans le code
6. Run → Debug → Sélectionner la configuration

<br>


#### VS Code

Créez .vscode/launch.json :
```json
{
    "version": "0.2.0",
    "configurations": [
        {
            "type": "java",
            "name": "Debug Backend Docker",
            "request": "attach",
            "hostName": "localhost",
            "port": 5005
        }
    ]
}
```
<br>

### Frontend (Angular)
Utilisez les Chrome DevTools :

1. Ouvrir http://localhost:4200 dans Chrome
2. F12 → Sources
3. Vos fichiers TypeScript sont disponibles dans `webpack://`

<br>

## 🩺 Troubleshooting
### Problème : MariaDB ne démarre pas

#### Symptômes : Status "Waiting" ou "Unhealthy"

#### Solutions :

```bash
# 1. Voir les logs
docker-compose -f docker-compose.dev.yml logs mariadb

# 2. Reset le volume de données
docker-compose -f docker-compose.dev.yml down -v
docker-compose -f docker-compose.dev.yml up

# 3. Tester le healthcheck manuellement
docker exec -it air-sphere-connect_db mysqladmin ping -h localhost
```
<br>

### Problème : Backend ne démarre pas
#### Symptômes : Erreur de connexion à MariaDB

#### Solutions :

```bash
# 1. Vérifier que MariaDB est healthy
docker-compose -f docker-compose.dev.yml ps

# 2. Voir les logs du backend
docker-compose -f docker-compose.dev.yml logs backend | tail -50

# 3. Vérifier la connexion depuis le backend
docker exec -it airsphere_backend ping mariadb
```
<br>

### Problème : Frontend ne charge pas

#### Symptômes : Page blanche ou erreur 404

#### Solutions :

```bash
# 1. Voir les logs
docker-compose -f docker-compose.dev.yml logs frontend

# 2. Vérifier que le serveur Angular tourne
curl http://localhost:4200

# 3. Rebuild le frontend
docker-compose -f docker-compose.dev.yml build frontend
docker-compose -f docker-compose.dev.yml up frontend
```
<br>

### Problème : Hot reload ne fonctionne pas

#### Solutions :

```bash
# 1. Vérifier les volumes
docker inspect airsphere_backend -f '{{ json .Mounts }}' | jq

# 2. Redémarrer le service
docker-compose -f docker-compose.dev.yml restart backend

# 3. Rebuild complet
docker-compose -f docker-compose.dev.yml down
docker-compose -f docker-compose.dev.yml up --build
```

<br>

### Problème : Port déjà utilisé

#### Symptômes : `Error starting userland proxy: listen tcp 0.0.0.0:8080: bind: address already in use`

#### Solutions :

```bash
# 1. Trouver le processus qui utilise le port
# Linux/macOS :
lsof -i :8080

# Windows :
netstat -ano | findstr :8080

# 2. Arrêter le processus OU changer le port dans .env
BACKEND_PORT=8081
```
<br>

### Problème : Permissions sur les fichiers

#### Symptômes (Linux) : Erreurs de permissions sur les volumes

#### Solutions :
```bash
# Donner les bonnes permissions
sudo chown -R $USER:$USER air-sphere-connect-back/
sudo chown -R $USER:$USER air-sphere-connect-front/
```
<br>

## ✅ Bonnes pratiques
### Développement quotidien
```bash
# Matin : Démarrer l'environnement
docker-compose -f docker-compose.dev.yml up -d

# Travailler normalement (hot reload actif)
# ...

# Soir : Arrêter l'environnement
docker-compose -f docker-compose.dev.yml down
```


### Avant un commit Git
```bash
# 1. Tester que tout compile
docker-compose -f docker-compose.dev.yml build

# 2. Vérifier que les tests passent
docker exec -it airsphere_backend mvn test

# 3. Vérifier le frontend
docker exec -it airsphere_frontend npm run lint
```

### Performance

✅ Utilisez les volumes nommés (déjà configurés)  
✅ Ne montez pas `node_modules/` ou `target/` depuis l'hôte  
✅ Utilisez **:ro** (read-only) pour les fichiers non modifiés par le conteneur  
✅ Limitez les fichiers synchronisés (uniquement **src/**)  

### Sécurité

❌ Ne commitez JAMAIS le fichier **.env**  
❌ Ne mettez JAMAIS de vraies clés API en clair  
✅ Utilisez **.env.example** comme template  
✅ Documentez les variables nécessaires  

<br>

## 📚 Ressources

[Documentation Docker  ](https://docs.docker.com/)  
[Docker Compose Reference ](https://docs.docker.com/reference/compose-file/)   
[Spring Boot Docker Guide ](https://spring.io/guides/gs/spring-boot-docker)   
[Angular Docker Guide](https://angular.io/guide/deployment#docker)    

<br>

## 🤝 Support
Pour toute question ou problème :

1. Consulter la section [Troubleshooting](#-troubleshooting)  
2. Vérifier les logs : [docker-compose -f docker-compose.dev.yml logs](https://claude.ai/chat/1e7d9b0d-7424-4315-a489-ab9673c25a03#-troubleshooting)
3. Contacter l'équipe : email@example.com !!! A MODIFIER !!!

<br>

**Bon développement ! 🚀**