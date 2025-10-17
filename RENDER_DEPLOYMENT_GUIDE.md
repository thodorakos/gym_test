# Render.com Deployment Guide

Complete step-by-step guide to deploy your Spring Boot + Nginx + MySQL application to Render.com.

## Architecture Overview

```
Internet → Nginx (Frontend Service) → Backend (Spring Boot API)
                  ↓
           MySQL Database (Managed)
```

- **Domain**: https://hrakleio-personal-training.gr/
- **Frontend Service**: Nginx serving static files + proxying API requests
- **Backend Service**: Spring Boot API running on port 8080
- **Database**: Managed MySQL database (Render's PostgreSQL alternative or external MySQL)

---

## Prerequisites

1. GitHub account with your repository pushed
2. Render.com account (free tier available)
3. Domain registered (hrakleio-personal-training.gr)
4. Docker Hub account (optional, for private image storage)

---

## STEP 1: Prepare Your Local Environment

### 1.1 Create `.env` file (LOCAL ONLY - never commit)

```bash
cd /home/thodorakos/gym_test
cp .env.example .env
```

Edit `.env` with your local development values:

```
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/gym?useSSL=false&allowPublicKeyRetrieval=true
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=rootpassword
SPRING_JPA_HIBERNATE_DDL_AUTO=update
FRONTEND_API_URL=http://localhost/api
ENVIRONMENT=development
```

### 1.2 Verify `.gitignore` includes `.env`

The `.env` file should NEVER be committed. Verify in your `.gitignore`:

```bash
cat .gitignore | grep ".env"
```

### 1.3 Push code to GitHub (without `.env`)

```bash
cd /home/thodorakos/gym_test
git add .
git commit -m "Prepare for Render.com deployment"
git push origin master
```

---

## STEP 2: Create MySQL Database on Render.com (Browser Steps)

### 2.1 Sign in to Render.com dashboard
- Go to https://dashboard.render.com
- Sign up or login with your GitHub account

### 2.2 Create MySQL Database
1. Click **"New"** button → **"Database"**
2. Select **"MySQL"**
3. **Name**: `gym-db` (or similar)
4. **Database name**: `gym`
5. **Region**: Choose closest to your users (Europe recommended for Greece)
6. **MySQL version**: 8.0 or latest
7. Keep other settings as default
8. Click **"Create Database"**

### 2.3 Save Database Credentials
After creation, you'll see connection details. **SAVE THESE SECURELY**:
- **External Database URL**: `mysql://username:password@hostname:3306/gym`
  - This is in format: `mysql://user:pass@host:port/dbname`
- **Internal URL**: (for services within Render - we'll use this)
- **Host**: `hostname`
- **Port**: `3306`
- **Database**: `gym`
- **Username**: `user_xxxxx`
- **Password**: (long random string)

⚠️ **IMPORTANT**: The MySQL service will only be accessible from your other Render services by default (firewall protection).

---

## STEP 3: Create Backend Service on Render.com (Browser Steps)

### 3.1 Create Web Service for Backend
1. Click **"New"** → **"Web Service"**
2. **Select Repository**: Choose your GitHub repo
3. **Name**: `gym-backend`
4. **Region**: Same as database
5. **Branch**: `master`
6. **Runtime**: `Docker`
7. **Build Command**: (leave empty - Docker will handle it)
8. **Start Command**: (leave empty - Docker will handle it)
9. Click **"Create Web Service"**

### 3.2 Configure Environment Variables
1. Go to your backend service settings
2. Scroll to **"Environment"** section
3. Click **"Add Environment Variable"** and add:

| Key | Value | Notes |
|-----|-------|-------|
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://INTERNAL_HOST:3306/gym?useSSL=true&serverTimezone=UTC` | Use the Internal hostname from Step 2.3 |
| `SPRING_DATASOURCE_USERNAME` | `your_db_username` | From Step 2.3 |
| `SPRING_DATASOURCE_PASSWORD` | `your_db_password` | From Step 2.3 - this is sensitive! |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | `update` | Allows schema auto-creation |
| `ENVIRONMENT` | `production` | |

### 3.3 Configure Dockerfile
1. In the Backend service settings, find **"Build & Deploy"**
2. Set **"Dockerfile Path"**: `backend/Dockerfile.production`
3. Save settings

### 3.4 Get Backend Service URL
After deployment completes, note the service URL. It will be something like:
- `https://gym-backend.onrender.com`

⚠️ **Note**: The backend is NOT accessible directly from the internet - only from the frontend service (internal network).

---

## STEP 4: Create Frontend Service on Render.com (Browser Steps)

### 4.1 Create Web Service for Frontend
1. Click **"New"** → **"Web Service"**
2. **Select Repository**: Same GitHub repo
3. **Name**: `gym-frontend`
4. **Region**: Same as backend and database
5. **Branch**: `master`
6. **Runtime**: `Docker`
7. Click **"Create Web Service"**

### 4.2 Configure Environment Variables
1. Go to Frontend service settings
2. Add environment variables:

| Key | Value |
|-----|-------|
| `FRONTEND_API_URL` | `http://gym-backend:8080/api` |
| `BACKEND_INTERNAL_URL` | `http://gym-backend:8080` |

**Note**: These URLs use service names (internal network), not the external URLs.

### 4.3 Configure Dockerfile
1. Set **"Dockerfile Path"**: `Dockerfile.nginx.production`
2. Set **"Dockerfile Context"**: `.` (root)
3. Save settings

### 4.4 Wait for Deployment
The service should deploy. You'll see the public URL like:
- `https://gym-frontend.onrender.com`

---

## STEP 5: Connect Custom Domain (Browser Steps)

### 5.1 Configure Frontend Service with Custom Domain
1. Go to your **Frontend service** settings
2. Scroll to **"Custom Domains"**
3. Click **"Add Custom Domain"**
4. Enter: `hrakleio-personal-training.gr`
5. Render will ask you to update your DNS

### 5.2 Update Your Domain's DNS Records
1. Go to your domain registrar (where you bought `hrakleio-personal-training.gr`)
2. Find DNS Settings
3. Add/Update these DNS records:

| Type | Name | Value | TTL |
|------|------|-------|-----|
| `A` | `@` or `hrakleio-personal-training.gr` | `IP_PROVIDED_BY_RENDER` | 3600 |
| `CNAME` | `www` | `gym-frontend.onrender.com` | 3600 |

**Render will provide the exact A record value** in the domain setup screen.

### 5.3 Verify SSL Certificate
- Render automatically provisions SSL/TLS certificate (Let's Encrypt)
- Your site will be automatically available at `https://hrakleio-personal-training.gr/`

### 5.4 Test Access
After DNS propagates (5-10 minutes):
```bash
curl https://hrakleio-personal-training.gr
curl https://hrakleio-personal-training.gr/api/health
```

---

## STEP 6: Update Your Backend for Production

### 6.1 Update Spring Boot CORS Configuration

Edit `backend/src/main/resources/application-production.properties`:

```properties
spring.web.cors.allowed-origins=https://hrakleio-personal-training.gr,https://www.hrakleio-personal-training.gr
```

### 6.2 Update Frontend API Endpoint

If your frontend makes API calls, update the base URL. For example, in your JavaScript:

```javascript
// Update your API_BASE_URL to production domain
const API_BASE_URL = 'https://hrakleio-personal-training.gr/api';
```

Or use the environment variable:
```javascript
const API_BASE_URL = process.env.FRONTEND_API_URL || '/api';
```

---

## Network & Firewall Configuration (Automatic on Render.com)

### What's Secured by Default:

1. **MySQL Database**
   - ✅ Only accessible from Render services (internal network)
   - ✅ NOT exposed to the internet
   - ✅ Firewall: Only services with correct credentials can access

2. **Backend Service**
   - ✅ Internal network communication with Frontend
   - ✅ NOT publicly accessible on port 8080
   - ✅ Only accessible through Nginx proxy on port 80/443

3. **Frontend/Nginx Service**
   - ✅ Publicly accessible on HTTPS (443)
   - ✅ Automatic SSL/TLS certificate
   - ✅ Redirects HTTP (80) to HTTPS (443)

### Port Configuration:

| Service | Internal Port | External Port | Access |
|---------|---|---|---|
| MySQL | 3306 | Not exposed | Render internal only |
| Backend | 8080 | Not exposed | Render internal only |
| Frontend/Nginx | 80 | 443 (HTTPS) | Public internet |

---

## STEP 7: Monitoring & Troubleshooting

### 7.1 View Logs
1. Go to each service dashboard
2. Click **"Logs"** to see real-time output
3. Check for deployment errors

### 7.2 Common Issues

**Problem**: Backend can't connect to database
- **Solution**: Verify `SPRING_DATASOURCE_URL` uses **internal** hostname (not external)
- Check database username/password are correct
- Ensure database service has finished initializing

**Problem**: Frontend can't reach backend API
- **Solution**: Verify `http://gym-backend:8080/api` in Nginx config
- Check network connectivity in logs
- Ensure backend service is running

**Problem**: CORS errors
- **Solution**: Update `spring.web.cors.allowed-origins` in production properties
- Include both domain and www subdomain

**Problem**: SSL certificate not working
- **Solution**: Render handles this automatically, just wait 5-10 minutes
- Check domain DNS propagation

### 7.3 Database Backups
On Render dashboard for MySQL service:
1. Click **"Backups"**
2. Set automatic backup retention
3. Manual backups available anytime

---

## STEP 8: Deploying Updates

### Automatic Deployment (Recommended)
1. Push code to GitHub `master` branch
2. Render automatically detects changes
3. Services rebuild and deploy automatically

### Manual Deployment
1. Go to service dashboard
2. Click **"Manual Deploy"** button
3. Select branch and deploy

---

## Security Checklist

✅ `.env` files are in `.gitignore`
✅ Database credentials stored as environment variables (not in code)
✅ MySQL only accessible internally
✅ Backend not directly exposed to internet
✅ HTTPS/SSL enabled for frontend
✅ CORS configured for your domain only
✅ Database auto-backups configured
✅ Sensitive logs not exposed

---

## Cost Estimation (Render.com Pricing)

- **Free Tier**: $0/month (includes 400 compute hours)
- **Web Services**: $7/month each (after free tier) = $14/month for 2 services
- **MySQL Database**: $15/month (or PostgreSQL free tier available)
- **Total**: ~$30/month for production setup

---

## Summary of Commands for Local Testing

Before final deployment, test locally:

```bash
cd /home/thodorakos/gym_test

# Build Docker images with production Dockerfiles
docker build -f backend/Dockerfile.production -t gym-backend:prod ./backend
docker build -f Dockerfile.nginx.production -t gym-nginx:prod .
docker build -f frontend/Dockerfile.production -t gym-frontend:prod ./frontend

# Run with docker-compose (if you create a production compose file)
docker-compose -f docker-compose.prod.yml up
```

---

## Support & Documentation

- Render Docs: https://render.com/docs
- Spring Boot Docs: https://spring.io/projects/spring-boot
- MySQL Docs: https://dev.mysql.com/doc/

---

## Next Steps

1. ✅ Push code with this guide to GitHub
2. ✅ Create MySQL database on Render
3. ✅ Create Backend service
4. ✅ Create Frontend service
5. ✅ Connect custom domain
6. ✅ Monitor logs and verify everything works
7. ✅ Set up backups and monitoring
