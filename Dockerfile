# Stage 1: Build frontend
FROM node:18 AS frontend-build
WORKDIR /app/frontend
COPY frontend/package*.json ./
RUN npm install
COPY frontend/ ./
RUN npm run build

# Stage 2: Build backend
FROM maven:3.8.5-openjdk-17 AS backend-build
WORKDIR /app/backend
COPY backend/pom.xml ./
COPY backend/mvnw .
COPY backend/mvnw.cmd .
COPY --from=backend-build /app/backend/.mvn ./.mvn
RUN ./mvnw dependency:go-offline
COPY backend/src ./src
RUN ./mvnw package -DskipTests

# Stage 3: Final image
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copy backend jar
COPY --from=backend-build /app/backend/target/*.jar app.jar

# Copy frontend build
COPY --from=frontend-build /app/frontend/dist /usr/share/nginx/html

# Copy nginx config and entrypoint
COPY nginx.conf /etc/nginx/nginx.conf
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

# Install nginx
RUN apt-get update && apt-get install -y nginx

EXPOSE 80

ENTRYPOINT ["/entrypoint.sh"]
