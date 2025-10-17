# Stage 1: Build the frontend# Stage 1: Build frontend

FROM node:18 AS frontendFROM node:18 AS frontend-build

WORKDIR /app/frontendWORKDIR /app/frontend

COPY frontend/ /app/frontendCOPY frontend/package*.json ./

RUN npm installRUN npm install

RUN npm run buildCOPY frontend/ ./

RUN npm run build

# Stage 2: Build the backend

FROM maven:3.8.4-openjdk-17 AS backend# Stage 2: Build backend

WORKDIR /app/backendFROM maven:3.8.5-openjdk-17 AS backend-build

COPY backend/ /app/backendWORKDIR /app/backend

RUN mvn clean installCOPY backend/pom.xml ./

COPY backend/mvnw .

# Stage 3: Create the final imageCOPY backend/mvnw.cmd .

FROM openjdk:17-jdk-slimCOPY --from=backend-build /app/backend/.mvn ./.mvn

WORKDIR /appRUN ./mvnw dependency:go-offline

COPY --from=backend /app/backend/target/*.jar app.jarCOPY backend/src ./src

COPY --from=frontend /app/frontend/dist /app/src/main/resources/staticRUN ./mvnw package -DskipTests

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]# Stage 3: Final image

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
