# Build stage
FROM node:20-alpine AS builder

WORKDIR /app

# Copier package.json et package-lock.json
COPY package*.json ./

# Installer les dépendances
RUN npm ci

# Copier le code source
COPY . .

# Build de l'application Angular en mode production
# Note: Ce Dockerfile est pour la PRODUCTION uniquement
# Pour le dev, utilisez Dockerfile.dev
RUN npm run build -- --configuration production

# Production stage
FROM nginx:alpine

# Installer curl et gettext (pour envsubst) pour health check et injection d'env
RUN apk add --no-cache curl gettext

# Copier les fichiers buildés (production ne crée pas de sous-dossier /browser)
COPY --from=builder /app/dist/air-sphere-connect-front /usr/share/nginx/html

# Configuration Nginx pour Angular (production)
COPY nginx.prod.conf /etc/nginx/conf.d/default.conf

# Copier le template d'environnement
COPY src/assets/env.template.js /usr/share/nginx/html/env.template.js

# Exposer le port
EXPOSE 4200

# Commande d'injection dynamique avant lancement de Nginx
CMD envsubst < /usr/share/nginx/html/env.template.js > /usr/share/nginx/html/env.js \
  && nginx -g 'daemon off;'
