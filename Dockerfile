# Stage 1: Build frontend
FROM node:18 AS frontend
WORKDIR /app/frontend
COPY frontend/package.json ./
RUN npm install
COPY frontend/ ./
RUN npm run build


# Stage 2: Build backend
FROM maven:3.8.4-openjdk-17 AS backend
WORKDIR /app
COPY backend/pom.xml ./
COPY backend/src ./src
RUN mvn clean install -DskipTests

# Stage 3: Create final image
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=backend /app/target/*.jar app.jar
COPY --from=frontend /app/frontend/dist /app/static
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]