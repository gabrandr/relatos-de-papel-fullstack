FROM node:20-alpine AS build
WORKDIR /app

COPY relatos-de-papel-frontend/package.json relatos-de-papel-frontend/package-lock.json ./
RUN npm ci

COPY relatos-de-papel-frontend/ .

ARG VITE_API_BASE_URL=http://localhost:8762
ENV VITE_API_BASE_URL=$VITE_API_BASE_URL

RUN npm run build

FROM nginx:1.27-alpine
COPY docker/frontend/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80
