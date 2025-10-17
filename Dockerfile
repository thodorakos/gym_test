# Stage 1: Build frontend
FROM node:18 AS frontend
WORKDIR /app/frontend
COPY frontend/package.json frontend/package-lock.json* ./
RUN npm install
COPY frontend/ ./
RUN npm run build

# Stage 2: Build backend
FROM maven:3.8.4-openjdk-17 AS backend
WORKDIR /app
COPY backend/pom.xml ./
COPY backend/mvnw ./
COPY backend/mvnw.cmd ./
COPY backend/.mvn ./.mvn
RUN ./mvnw dependency:go-offline
COPY backend/src ./src
RUN ./mvnw package -DskipTests

# Stage 3: Create final image
FROM openjdk:17-jdk-slim
WORKDIR /app
# Copy frontend assets
COPY --from=frontend /app/frontend/dist/ /app/src/main/resources/static/
# Copy backend jar
COPY --from=backend /app/target/*.jar app.jar
# Copy static assets from frontend that are not part of the build
COPY frontend/index.html /app/src/main/resources/static/
COPY frontend/admin.html /app/src/main/resources/static/
COPY frontend/sessions.html /app/src/main/resources/static/
COPY frontend/signin.html /app/src/main/resources/static/
COPY frontend/signup.html /app/src/main/resources/static/
COPY frontend/js /app/src/main/resources/static/js

EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
