FROM node:24-alpine AS build

WORKDIR /app

COPY package*.json ./

RUN npm ci --ignore-scripts

COPY . .

ARG VITE_API_URL
ARG VITE_IMAGE_CONTAINER_URL
ARG VITE_STORAGE_URL

ENV VITE_API_URL=$VITE_API_URL
ENV VITE_IMAGE_CONTAINER_URL=$VITE_IMAGE_CONTAINER_URL
ENV VITE_STORAGE_URL=$VITE_STORAGE_URL

RUN npm run build

FROM nginxinc/nginx-unprivileged:alpine3.23-perl AS runtime

COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 8080

CMD ["nginx", "-g", "daemon off;"]