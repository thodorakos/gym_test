# Stage 1: Build the frontend
FROM node:18 AS frontend
WORKDIR /app/frontend
COPY frontend/ /app/frontend
RUN npm install
RUN npm run build

# Stage 2: Build the backend
FROM maven:3.8.4-openjdk-17 AS backend
WORKDIR /app/backend
COPY backend/ /app/backend
RUN mvn clean install

# Stage 3: Create the final image
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=backend /app/backend/target/*.jar app.jar
COPY --from=frontend /app/frontend/dist /app/src/main/resources/static
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]COPY nginx.conf /etc/nginx/nginx.conf
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

# Install nginx
RUN apt-get update && apt-get install -y nginx

EXPOSE 80

ENTRYPOINT ["/entrypoint.sh"]
