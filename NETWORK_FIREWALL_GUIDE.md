# Network & Firewall Configuration Guide

## Render.com Network Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        Internet (Public)                        │
│                                                                  │
│  Your Domain: https://hrakleio-personal-training.gr            │
└─────────────────────────────────────────────────────────────────┘
                                 ▲
                                 │ (HTTPS - Port 443)
                                 │
                    ┌────────────▼────────────┐
                    │  Nginx / Frontend       │
                    │  (Public Service)       │
                    │  - Serves static HTML   │
                    │  - Proxies /api to      │
                    │    Backend              │
                    └────────────┬────────────┘
                                 │
          ┌──────────────────────┼──────────────────────┐
          │                      │                      │
          │  (Internal Network - Render Private Network)
          │
    ┌─────▼──────┐        ┌──────▼──────┐
    │  Backend    │        │  MySQL DB   │
    │  (Private)  │        │  (Private)  │
    │  Port 8080  │        │  Port 3306  │
    └─────┬──────┘        └──────┬──────┘
          │                      │
          └──────────────────────┘
          (Internal communication)
```

---

## Firewall Rules (Render Handles Automatically)

### MySQL Database Service
| Direction | Source | Port | Protocol | Action | Notes |
|-----------|--------|------|----------|--------|-------|
| Inbound | Backend service (gym-backend) | 3306 | TCP | ALLOW | Only from backend |
| Inbound | External | 3306 | TCP | BLOCK | Not exposed to internet |
| Outbound | N/A | All | All | ALLOW | Can reach anywhere (if needed) |

### Backend Service
| Direction | Source | Port | Protocol | Action | Notes |
|-----------|--------|------|----------|--------|-------|
| Inbound | Frontend service | 8080 | TCP | ALLOW | Internal network only |
| Inbound | Internet | 8080 | TCP | BLOCK | Not publicly accessible |
| Outbound | MySQL | 3306 | TCP | ALLOW | Database access |
| Outbound | Internet | Any | All | ALLOW | (if needed for external APIs) |

### Frontend/Nginx Service
| Direction | Source | Port | Protocol | Action | Notes |
|-----------|--------|------|----------|--------|-------|
| Inbound | Internet | 80 | TCP | ALLOW | HTTP → HTTPS redirect |
| Inbound | Internet | 443 | TCP | ALLOW | HTTPS (SSL/TLS) |
| Outbound | Backend | 8080 | TCP | ALLOW | Proxy requests |
| Outbound | Internet | Any | All | ALLOW | (if needed for CDN, etc.) |

---

## Port Configuration

### What Each Port Does

| Port | Service | Type | Public | Purpose |
|------|---------|------|--------|---------|
| **80** | Nginx | HTTP | Yes | Redirects to HTTPS |
| **443** | Nginx | HTTPS | Yes | Secure web access (SSL/TLS) |
| **8080** | Backend (Java) | HTTP | Internal only | REST API (Spring Boot) |
| **3306** | MySQL | TCP | Internal only | Database (MySQL protocol) |

### Port Binding

```
Internet (Port 443)
    ↓
Nginx Container (Port 80 & 443 bound to service)
    ├→ Serves frontend static files (Port 80 internally)
    └→ Proxies /api/* to Backend (Port 8080 internally)

Backend Container (Port 8080 bound to service)
    ├→ Listens on 8080 internally
    └→ Only accessible from Nginx (internal network)

MySQL Container (Port 3306 bound to service)
    ├→ Listens on 3306 internally
    └→ Only accessible from Backend (internal network)
```

---

## Network Connectivity

### Service to Service Communication (Internal Network)

Inside Render's network, services communicate using **service names as hostnames**:

```
Frontend Nginx Config:
    location /api/ {
        proxy_pass http://gym-backend:8080/api/;
        # Uses service name "gym-backend" (DNS resolves internally)
    }

Backend Spring Config:
    SPRING_DATASOURCE_URL=jdbc:mysql://gym-db-internal-host:3306/gym
    # Uses database service's internal hostname
```

### Why Internal Hostnames?

- ✅ **Security**: Services don't expose external URLs to each other
- ✅ **Privacy**: Database not accessible from internet
- ✅ **Performance**: Lower latency (same data center)
- ✅ **Encryption**: Communication within Render's private network

---

## DNS & Domain Configuration

### How Your Domain Routes to Render

```
User enters: https://hrakleio-personal-training.gr
    ↓
1. DNS lookup at registrar
    ↓
2. A record points to Render's IP: 123.45.67.89 (example)
    ↓
3. Browser connects to HTTPS://123.45.67.89
    ↓
4. Render's load balancer routes to your Frontend service
    ↓
5. Nginx serves content or proxies to Backend
```

### DNS Records Setup

You need to update your domain registrar (where you bought the domain):

```
Record 1 (Main domain):
  Type: A
  Name: @ (or hrakleio-personal-training.gr)
  Value: [IP_ADDRESS_PROVIDED_BY_RENDER]
  TTL: 3600 (1 hour)

Record 2 (www subdomain - optional):
  Type: CNAME
  Name: www
  Value: hrakleio-personal-training.gr
  TTL: 3600 (1 hour)
```

### How to Update DNS at Registrar

**GoDaddy**:
1. Login → Domain Management
2. Find your domain
3. Click "DNS" → "Manage DNS"
4. Add/Edit A record with Render's IP

**Namecheap**:
1. Login → Dashboard
2. Click domain name
3. Advanced DNS
4. Add/Edit A record with Render's IP

**HostGator** / **Bluehost** / **Similar**:
1. cPanel login
2. Zone Editor
3. Add/Edit A record

---

## SSL/TLS Certificate (HTTPS)

### Automatic Setup by Render

1. When you add custom domain, Render automatically:
   - Provisions SSL certificate (Let's Encrypt)
   - Enables HTTPS on port 443
   - Redirects HTTP (80) to HTTPS (443)

2. Certificate auto-renews 30 days before expiration

3. No configuration needed on your part! ✅

### Verification

```bash
# Check certificate validity
curl -v https://hrakleio-personal-training.gr

# Should show:
# HTTP/2 200 (or 301 redirect if HTTP)
# Subject: CN = hrakleio-personal-training.gr
# Issuer: Let's Encrypt
```

---

## CORS Configuration

### Why CORS Matters

Your frontend and backend are on the **same domain** but different ports internally.
When frontend JavaScript calls backend API, the browser enforces CORS.

### Spring Boot CORS Config (Backend)

File: `backend/src/main/resources/application-production.properties`

```properties
# Allow requests from your domain
spring.web.cors.allowed-origins=https://hrakleio-personal-training.gr,https://www.hrakleio-personal-training.gr

# Allow HTTP methods
spring.web.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS

# Allow headers
spring.web.cors.allowed-headers=*

# Allow credentials (cookies, auth headers)
spring.web.cors.allow-credentials=true
```

### Testing CORS

```bash
# From browser console:
fetch('https://hrakleio-personal-training.gr/api/users')
  .then(r => r.json())
  .then(d => console.log(d))
  .catch(e => console.error('CORS Error:', e))
```

---

## Reverse Proxy (Nginx) Configuration

### How Nginx Routes Traffic

File: `nginx.conf.production`

```nginx
# Frontend routes: serve static files
location / {
    proxy_pass http://frontend:80;
    # Nginx receives request, forwards to frontend service on port 80
}

# API routes: proxy to backend
location /api/ {
    proxy_pass http://backend:8080/api/;
    # Nginx receives /api/* request, forwards to backend:8080/api/*
}
```

### Important Headers Nginx Sets

```nginx
proxy_set_header Host $host;
# Tells backend which domain was requested

proxy_set_header X-Real-IP $remote_addr;
# Tells backend the client's real IP

proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
# Chain of IPs showing request path

proxy_set_header X-Forwarded-Proto $scheme;
# Tells backend it was HTTPS (even though proxy uses HTTP internally)
```

---

## Security Best Practices

### ✅ What We Have

1. **Database Isolation**
   - MySQL only accessible from backend service
   - Not exposed to internet
   - No direct database access from frontend

2. **Backend Isolation**
   - Backend only accessible from Nginx
   - Not directly accessible from internet
   - Only via API endpoints

3. **HTTPS/SSL**
   - All public traffic encrypted
   - Automatic certificate management
   - HTTP redirects to HTTPS

4. **Firewall**
   - Render automatically blocks unauthorized traffic
   - Services communicate via internal network
   - No exposed database ports

5. **Credentials**
   - Database credentials in environment variables
   - Not in code or git
   - Not exposed in logs (configure logging level)

### ⚠️ What to Monitor

- Database credentials in Spring logs (set `logging.level.org.hibernate.SQL=INFO` not DEBUG)
- Stack traces exposing internal paths
- API responses leaking sensitive data
- CORS misconfiguration

---

## Troubleshooting Network Issues

### Issue: Frontend can't reach backend API

**Symptoms**: 
- Network error in browser console
- 502 Bad Gateway error

**Check**:
```
1. Is backend service running? (Check Render dashboard)
2. Is Nginx using correct backend URL? (Should be http://gym-backend:8080)
3. Are both services in same region?
4. Check Nginx logs for proxy errors
```

### Issue: Can't connect to database

**Symptoms**:
- Backend service crashes
- "Cannot connect to MySQL" in logs

**Check**:
```
1. Is database service running?
2. Is SPRING_DATASOURCE_URL using INTERNAL hostname (not external)?
3. Are credentials correct?
4. Check firewall allows backend → database connection
```

### Issue: CORS errors in browser

**Symptoms**:
- `Access-Control-Allow-Origin` error in console
- Frontend can't call API

**Fix**:
```
Update CORS configuration in:
backend/src/main/resources/application-production.properties

Add your domain:
spring.web.cors.allowed-origins=https://hrakleio-personal-training.gr
```

### Issue: DNS not resolving

**Symptoms**:
- `Cannot resolve hrakleio-personal-training.gr`
- Domain not reachable

**Fix**:
```bash
# Wait 5-10 minutes for DNS propagation

# Check DNS:
nslookup hrakleio-personal-training.gr
dig hrakleio-personal-training.gr

# Should return Render's IP address
```

---

## Performance Optimization

### Connection Timeouts
```nginx
# Increase if backend is slow:
proxy_connect_timeout 60s;
proxy_send_timeout 60s;
proxy_read_timeout 60s;
```

### Connection Pooling
```properties
# In application.properties:
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
```

### Caching
```nginx
# Cache static files (CSS, JS, images):
location ~* \.(css|js|png|jpg|gif|ico)$ {
    expires 30d;
    add_header Cache-Control "public, immutable";
}
```

---

## Reference URLs

- Render Docs: https://render.com/docs
- Spring Boot CORS: https://spring.io/guides/gs/cors-rest-service/
- Nginx Proxy: https://nginx.org/en/docs/http/ngx_http_proxy_module.html
- SSL Certificate: https://letsencrypt.org/
