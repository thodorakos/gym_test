#!/bin/bash
# Script to build production Docker images locally for testing

set -e

echo "🏗️  Building production Docker images..."

# Build Backend
echo "📦 Building backend image..."
docker build -f backend/Dockerfile.production -t gym-backend:production ./backend

# Build Frontend
echo "📦 Building frontend image..."
docker build -f frontend/Dockerfile.production -t gym-frontend:production ./frontend

# Build Nginx
echo "📦 Building nginx image..."
docker build -f Dockerfile.nginx.production -t gym-nginx:production .

echo "✅ All production images built successfully!"
echo ""
echo "To test locally, run:"
echo "  docker-compose -f docker-compose.production.yml up"
echo ""
echo "Or run individual containers:"
echo "  docker run -d --name mysql gym-mysql:8.0 -e MYSQL_ROOT_PASSWORD=test -e MYSQL_DATABASE=gym"
echo "  docker run -d --name backend -p 8080:8080 -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/gym gym-backend:production"
echo "  docker run -d --name frontend -p 80:80 gym-frontend:production"
