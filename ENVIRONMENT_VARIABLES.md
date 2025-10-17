# Render.com Environment Configuration Checklist

## Critical Security Notes

⚠️ **NEVER push real secrets to GitHub**
⚠️ **NEVER commit `.env` files**
⚠️ **ALWAYS use Render.com dashboard to set environment variables**

---

## Backend Environment Variables (Set in Render Dashboard)

These must be configured in your Backend service on Render.com:

### Database Connection
```
SPRING_DATASOURCE_URL=jdbc:mysql://[INTERNAL_HOST]:3306/gym?useSSL=true&serverTimezone=UTC
SPRING_DATASOURCE_USERNAME=render_db_user
SPRING_DATASOURCE_PASSWORD=render_db_password
```

**How to get these values:**
1. Go to Render MySQL service dashboard
2. Look for "Connections" or "Database Info"
3. Copy the **Internal Connection String** (not External!)
4. Extract Host, Username, Password from the connection string

### Spring Boot Configuration
```
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_PROFILES_ACTIVE=production
```

---

## Frontend Environment Variables (Set in Render Dashboard)

These must be configured in your Frontend service on Render.com:

### API Configuration
```
BACKEND_INTERNAL_URL=http://gym-backend:8080
FRONTEND_API_URL=http://gym-backend:8080/api
```

**Note**: Use **internal service names** (gym-backend), not external URLs!

---

## Local Development (`.env` file - NOT committed)

For local testing, create `/home/thodorakos/gym_test/.env`:

```bash
# Local MySQL Connection (docker-compose)
SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/gym?useSSL=false&allowPublicKeyRetrieval=true
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=rootpassword
SPRING_JPA_HIBERNATE_DDL_AUTO=update

# Local Frontend Configuration
FRONTEND_API_URL=http://localhost/api
ENVIRONMENT=development
```

---

## Firewalls & Network Configuration

### MySQL Database Service
- **Inbound**: Only from Render services (automatic)
- **Outbound**: MySQL port 3306
- **Status**: Protected ✅ (not exposed to public internet)

### Backend Service
- **Inbound**: Only from Frontend service (http://gym-backend:8080)
- **Outbound**: MySQL database (port 3306)
- **Status**: Protected ✅ (not publicly accessible)

### Frontend/Nginx Service
- **Inbound**: Public internet on HTTPS (443) and HTTP (80)
- **Outbound**: Backend service (http://gym-backend:8080)
- **Status**: Public ✅ (exposed to internet with SSL/TLS)

---

## Ports Configuration

| Service | Port | Type | Access | Firewall |
|---------|------|------|--------|----------|
| MySQL | 3306 | TCP | Internal only | Blocked from public |
| Backend | 8080 | TCP | Internal (Render network) | Blocked from public |
| Frontend (HTTP) | 80 | TCP | Redirect to 443 | HTTP→HTTPS redirect |
| Frontend (HTTPS) | 443 | TCP | Public | Open ✅ |

---

## Step-by-Step Render Configuration

### 1. MySQL Service Setup

In Render Dashboard:
1. Create MySQL service
2. Note down these values:
   - **External Connection**: mysql://user:pass@host:3306/gym (for admin access)
   - **Internal Connection**: mysql://user:pass@internal-host:3306/gym (for backend service)
   - **Database**: gym
   - **Port**: 3306

### 2. Backend Service Setup

In Render Dashboard → Backend Service → Environment Variables:

```
Name: SPRING_DATASOURCE_URL
Value: jdbc:mysql://[INTERNAL_HOST]:3306/gym?useSSL=true&serverTimezone=UTC

Name: SPRING_DATASOURCE_USERNAME
Value: [DB_USERNAME]

Name: SPRING_DATASOURCE_PASSWORD
Value: [DB_PASSWORD]

Name: SPRING_JPA_HIBERNATE_DDL_AUTO
Value: update

Name: SPRING_PROFILES_ACTIVE
Value: production
```

**Dockerfile**: `backend/Dockerfile.production`

**Port**: 8080 (automatically configured)

### 3. Frontend Service Setup

In Render Dashboard → Frontend Service → Environment Variables:

```
Name: BACKEND_INTERNAL_URL
Value: http://gym-backend:8080

Name: FRONTEND_API_URL
Value: http://gym-backend:8080/api
```

**Dockerfile**: `Dockerfile.nginx.production`

**Port**: 80 → Render auto-redirects to HTTPS 443

### 4. Domain Configuration

In Render Dashboard → Frontend Service → Custom Domains:
1. Add domain: `hrakleio-personal-training.gr`
2. Get A-record from Render
3. Update DNS at your registrar
4. SSL certificate auto-provisions (Let's Encrypt)

---

## Debugging Environment Variables

### Check if variables are loaded

SSH into your service (if available) or check logs:

```bash
# In Render logs, backend should show:
"Database URL: jdbc:mysql://..."
"Active Profile: production"
```

### Common Issues

**Issue**: Database connection failed
- **Check**: Is `SPRING_DATASOURCE_URL` using INTERNAL hostname?
- **Fix**: Don't use external connection URL

**Issue**: Backend service crashes
- **Check**: Are all required environment variables set?
- **Fix**: Add missing variables in Render dashboard

**Issue**: Frontend can't reach backend
- **Check**: Is `BACKEND_INTERNAL_URL=http://gym-backend:8080`?
- **Fix**: Use service name (gym-backend) not external URL

---

## Production vs Development

| Setting | Development | Production |
|---------|---|---|
| Database Host | `localhost` or `db` | Render internal host |
| SSL | No (HTTP) | Yes (HTTPS) |
| CORS Origins | `http://localhost:*` | `https://hrakleio-personal-training.gr` |
| Logging | DEBUG | INFO |
| Hibernate DDL | `update` | `update` (or `validate`) |
| Spring Profile | default | `production` |

---

## Emergency: Reset Database

If you need to reset the database on Render:

1. Go to MySQL service
2. Click "Reset" or delete and recreate
3. Backend will auto-create schema with `SPRING_JPA_HIBERNATE_DDL_AUTO=update`
4. Verify with logs

---

## Local Testing Before Deploying

Test production build locally:

```bash
# Create local .env with test values
cp .env.example .env

# Edit .env with local values
# Then run:
docker-compose -f docker-compose.production.yml up

# Visit: http://localhost
# API test: curl http://localhost/api/health
```

---

## References

- Spring Boot Environment Variables: https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config
- Render Documentation: https://render.com/docs/env-vars
- MySQL Connection Strings: https://dev.mysql.com/doc/connector-j/en/connector-j-reference-jdbc-url-format.html
