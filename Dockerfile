# Stage 1: Build frontend
FROM node:18 AS frontend
WORKDIR /app/frontend
COPY frontend/package.json ./
COPY frontend/package-lock.json* ./
RUN npm install --only=prod
COPY frontend/ ./
RUN npm run build

# Stage 2: Build backend with frontend assets
FROM maven:3.8.4-openjdk-17 AS backend
WORKDIR /app
# Copy backend source
COPY backend/pom.xml ./
COPY backend/src ./src
# Copy entire frontend public folder (html, js, css, images)
COPY frontend/public/ ./src/main/resources/static/
# Build the backend. The JAR will now include the static files.
RUN mvn clean install -DskipTests

# Stage 3: Create final image
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=backend /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]