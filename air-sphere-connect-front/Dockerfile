# Build stage
FROM node:20-alpine AS builder

WORKDIR /app

# Copier package.json et package-lock.json
COPY package*.json ./

# Installer les dépendances
RUN npm ci

# Copier le code source
COPY . .

# Build de l'application Angular
RUN npm run build -- --configuration production

# Production stage
FROM nginx:alpine

# ✅ Installer curl pour health check
RUN apk add --no-cache curl

# Copier les fichiers buildés
COPY --from=builder /app/dist/air-sphere-connect-front /usr/share/nginx/html

# Configuration Nginx pour Angular
COPY nginx.conf /etc/nginx/conf.d/default.conf

# ✅ Exposer le port 4200 (pas 80) pour cohérence avec docker-compose
EXPOSE 4200

# Démarrer Nginx sur le port 4200
CMD ["nginx", "-g", "daemon off;"]
