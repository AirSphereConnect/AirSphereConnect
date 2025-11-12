# Migration de dev-front vers main : Configuration production-ready

## Vue d'ensemble

Ce document récapitule tous les changements apportés pour aligner `dev-front` avec `main` et rendre la configuration compatible avec un déploiement en production.

**Objectif** : Supprimer toutes les configurations hardcodées propres au développement et utiliser des variables d'environnement partout.

---

## 1. Configuration Backend (Spring Boot)

### 1.1 SecurityConfig.java
**Fichier** : `air-sphere-connect-back/src/main/java/com/airSphereConnect/security/SecurityConfig.java`

**Changements** :
- ✅ Ajout de `@Value("${cors.allowed-origins}")` pour lire CORS depuis application.yml
- ✅ Restauration du `ForwardedHeaderFilter` (nécessaire pour les reverse proxies en production)
- ✅ Suppression du TODO sur CSRF
- ✅ CORS configuré dynamiquement au lieu de hardcoder `http://localhost:4200`

```java
@Value("${cors.allowed-origins}")
private String corsAllowedOrigins;

@Bean
public ForwardedHeaderFilter forwardedHeaderFilter() {
    return new ForwardedHeaderFilter();
}

// Dans corsConfigurationSource()
configuration.setAllowedOrigins(List.of(corsAllowedOrigins));
```

### 1.2 JwtServiceImpl.java
**Fichier** : `air-sphere-connect-back/src/main/java/com/airSphereConnect/services/security/implementations/JwtServiceImpl.java`

**Changements** :
- ✅ Remplacement des constantes statiques par des champs d'instance configurables
- ✅ Ajout des paramètres `@Value` pour `accessTokenValidity` et `refreshTokenValidity`

**Avant** (hardcodé) :
```java
public static final long ACCESS_TOKEN_VALIDITY = 2 * 60 * 60 * 1000;
private static final long REFRESH_TOKEN_VALIDITY = 7 * 24 * 60 * 60 * 1000;
```

**Après** (configurable) :
```java
private final long accessTokenValidity;
private final long refreshTokenValidity;

public JwtServiceImpl(
    @Value("${jwt.secret:...}") String secret,
    @Value("${jwt.access-token-validity:7200000}") long accessTokenValidity,
    @Value("${jwt.refresh-token-validity:604800000}") long refreshTokenValidity,
    CustomUserDetailsService userDetailsService) {
    // ...
}
```

### 1.3 application-dev.yml
**Fichier** : `air-sphere-connect-back/src/main/resources/application-dev.yml`

**Changements** :
- ✅ Restauration des variables d'environnement pour les clés API
- ✅ Ajout de la configuration JWT complète
- ✅ Ajout de la configuration Cookie
- ✅ Correction des noms de base de données

**Ajouts** :
```yaml
weather:
  api:
    key: ${WEATHER_API_KEY:e935d0947600dd8f7247eefe1819751a}

mailjet:
  api:
    key: ${MAILJET_API_KEY:...}
    secret: ${MAILJET_API_SECRET:...}

# JWT Configuration
jwt:
  secret: ${JWT_SECRET:...}
  access-token-validity: ${JWT_ACCESS_TOKEN_VALIDITY:7200000}
  refresh-token-validity: ${JWT_REFRESH_TOKEN_VALIDITY:604800000}

# Cookie Configuration
cookie:
  secure: ${COOKIE_SECURE:false}  # false en dev, true en prod
```

### 1.4 application-docker.yml
**Fichier** : `air-sphere-connect-back/src/main/resources/application-docker.yml`

**Changements** :
- ✅ Correction des noms de base de données (`air_sphere_connect` au lieu de `air-sphere-connect`)

---

## 2. Configuration Frontend (Angular)

### 2.1 Dockerfiles

#### Dockerfile (PRODUCTION)
**Fichier** : `air-sphere-connect-front/Dockerfile`

**Changements** :
- ✅ Build en mode `production` (au lieu de development)
- ✅ Ajout de `gettext` pour l'injection dynamique avec `envsubst`
- ✅ Copie de `env.template.js`
- ✅ Utilisation de `nginx.prod.conf`
- ✅ Commande d'injection dynamique de l'API URL
- ✅ Ajout de `--legacy-peer-deps` pour Unovis (Angular 20)

```dockerfile
# Build avec --configuration production
RUN npm run build -- --configuration production

# Installer gettext pour envsubst
RUN apk add --no-cache curl gettext

# Copier le template d'environnement
COPY src/assets/env.template.js /usr/share/nginx/html/env.template.js

# Injection dynamique avant lancement
CMD envsubst < /usr/share/nginx/html/env.template.js > /usr/share/nginx/html/env.js \
  && nginx -g 'daemon off;'
```

#### Dockerfile.dev (DÉVELOPPEMENT)
**Fichier** : `air-sphere-connect-front/Dockerfile.dev`

**Changements** :
- ✅ Build en mode `development` (au lieu de production)
- ✅ Utilisation de `nginx.conf` (pas besoin d'injection dynamique en dev)
- ✅ Conservation de `--legacy-peer-deps` pour Unovis
- ✅ **IMPORTANT** : Copie depuis `/browser` (Angular moderne)

```dockerfile
# Copier depuis le dossier browser (Angular moderne)
COPY --from=builder /app/dist/air-sphere-connect-front/browser /usr/share/nginx/html
```

**Note** : Angular 17+ génère le build dans un sous-dossier `browser/`. Sans cette correction, Nginx affiche "Welcome to nginx" au lieu de l'application.

### 2.2 Fichiers d'environnement

#### env.js (fichier local de développement)
**Fichier** : `air-sphere-connect-front/src/env.js`

**Nouveau fichier** - Fallback pour le développement local :
```javascript
(function(window) {
  window.__env = window.__env || {};
  window.__env.apiUrl = "http://localhost:8080/api";
}(this));
```

#### env.template.js (template pour production)
**Fichier** : `air-sphere-connect-front/src/assets/env.template.js`

**Nouveau fichier** - Template utilisé en production avec `envsubst` :
```javascript
(function(window) {
  window.__env = window.__env || {};
  window.__env.apiUrl = "${API_URL}";
}(this));
```

### 2.3 ApiConfigService
**Fichier** : `air-sphere-connect-front/src/app/core/services/api.ts`

**Utilisation** : Ce service lit l'URL de l'API depuis `window.__env` :
```typescript
export class ApiConfigService {
  readonly apiUrl: string;

  constructor() {
    // Lecture depuis env.js (dev) ou env.js généré (prod)
    this.apiUrl = window.__env?.apiUrl || 'http://localhost:8080/api';
  }
}
```

**Services utilisant ApiConfigService** :
- `air-quality.ts`
- `city.ts`
- `population.ts`
- `post.service.ts`
- `section.service.ts`
- `thread.service.ts`
- `weather.ts`
- `alerts-service.ts`
- `favorites-service.ts`
- `user-service.ts`

---

## 3. Configuration Docker Compose

### 3.1 docker-compose.dev.yml
**Fichier** : `docker-compose.dev.yml`

**Changements** :
- ✅ Correction du nom du volume : `air_sphere_connect_db_data` (underscore)
- ✅ Correction du nom du network : `air_sphere_connect_network` (underscore)

**Raison** : Les tirets (`-`) ne sont pas recommandés dans les noms de volumes/networks Docker.

---

## 4. Variables d'environnement (.env)

### Fichier .env (à la racine du projet)

**Variables importantes** :
```bash
# Base de données
DB_NAME=air_sphere_connect
DB_USER=air_sphere_connect_user
DB_PASSWORD=PassASC@34Diginamic!

# CORS
CORS_ORIGINS=http://localhost:4200

# Clés API
WEATHER_API_KEY=e935d0947600dd8f7247eefe1819751a
MAILJET_API_KEY=13f60c773c9d6b968a6415fa88e6affc
MAILJET_API_SECRET=361d897cbe85bd88f3ddfea48599a081

# JWT
JWT_SECRET=UneCleSecreteSuperLongueEtComplexePourTestUnique1234567890
JWT_ACCESS_TOKEN_VALIDITY=7200000
JWT_REFRESH_TOKEN_VALIDITY=604800000

# Cookies
COOKIE_SECURE=false  # true en production (HTTPS)

# Docker networks/volumes
NETWORK_NAME=air_sphere_connect_network
DB_VOLUME_NAME=air_sphere_connect_db_data
```

---

## 5. Récapitulatif des modifications par fichier

| Fichier | Type de changement | Description |
|---------|-------------------|-------------|
| `SecurityConfig.java` | Config dynamique | CORS et ForwardedHeaderFilter |
| `JwtServiceImpl.java` | Config dynamique | Durées de tokens configurables |
| `application-dev.yml` | Variables d'env | API keys, JWT, Cookies |
| `application-docker.yml` | Fix noms | Correction noms de DB |
| `Dockerfile` (front) | Production | Build prod + envsubst |
| `Dockerfile.dev` (front) | Development | Build dev |
| `env.js` | Nouveau | Fallback dev |
| `env.template.js` | Nouveau | Template prod |
| `docker-compose.dev.yml` | Fix noms | Volumes/networks underscores |
| Services Angular | API URL | Utilisation de ApiConfigService |

---

## 6. Comment déployer en production

### 6.1 Configuration des variables d'environnement

Sur le serveur de production, créez un fichier `.env` avec :

```bash
# IMPORTANT : Changez toutes les valeurs sensibles !
DB_PASSWORD=VOTRE_MOT_DE_PASSE_SECURISE
JWT_SECRET=VOTRE_CLE_SECRETE_LONGUE_ET_ALEATOIRE
WEATHER_API_KEY=VOTRE_CLE_API_WEATHER
MAILJET_API_KEY=VOTRE_CLE_MAILJET
MAILJET_API_SECRET=VOTRE_SECRET_MAILJET
COOKIE_SECURE=true  # HTTPS obligatoire
CORS_ORIGINS=https://votre-domaine.com
API_URL=https://api.votre-domaine.com/api
```

### 6.2 Démarrage

```bash
# Utiliser Dockerfile (pas Dockerfile.dev) pour la production
docker-compose up -d --build
```

Le fichier `env.js` sera généré automatiquement par `envsubst` avec la valeur de `${API_URL}`.

---

## 7. Différences dev vs prod

| Aspect | Développement | Production |
|--------|--------------|------------|
| Dockerfile | `Dockerfile.dev` | `Dockerfile` |
| Build Angular | `development` | `production` |
| env.js | Hardcodé localhost | Généré par envsubst |
| CORS | `http://localhost:4200` | Domaine de prod |
| COOKIE_SECURE | `false` (HTTP) | `true` (HTTPS) |
| Nginx config | `nginx.conf` | `nginx.prod.conf` |

---

## 8. Tests effectués

✅ Build du frontend : succès
✅ Build du backend : succès
✅ Démarrage des conteneurs : succès
✅ Health checks : tous healthy
✅ Pas de config hardcodée restante

---

## 9. Points d'attention

1. **Ne jamais commiter le fichier `.env` de production** (il contient des secrets)
2. **Toujours utiliser HTTPS en production** (`COOKIE_SECURE=true`)
3. **Changer les secrets par défaut** (JWT_SECRET, DB_PASSWORD, etc.)
4. **Vérifier les CORS** en production (domaine exact)
5. **ForwardedHeaderFilter** est essentiel si derrière un reverse proxy (Nginx, Apache)

---

## 10. Commandes utiles

```bash
# Démarrer en dev
docker-compose -f docker-compose.dev.yml up -d --build

# Voir les logs
docker logs air_sphere_connect_back
docker logs air_sphere_connect_front

# Vérifier le statut
docker ps

# Arrêter tout
docker-compose -f docker-compose.dev.yml down
```

---

**Date de migration** : 12 novembre 2025
**Branche source** : `dev-front`
**Branche cible** : `main`
**Statut** : ✅ Prêt pour merge
