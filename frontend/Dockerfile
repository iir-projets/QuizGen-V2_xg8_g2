# Étape 1 : Build Angular
FROM node:18-alpine AS build
WORKDIR /app

# Copier les fichiers de dépendance et installer
COPY package*.json ./
RUN npm install

# Copier tout le projet et build
COPY . .
RUN npm run build -- --output-path=dist

# Étape 2 : Serveur Nginx pour l'app Angular
FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
