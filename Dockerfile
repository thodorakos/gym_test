# Stage 1: Build the frontend
FROM node:18 AS frontend
WORKDIR /app/frontend
COPY frontend/package*.json ./
RUN npm install
COPY frontend/ .
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
ENTRYPOINT ["java","-jar","app.jar"]
