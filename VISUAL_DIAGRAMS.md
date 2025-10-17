# Visual Deployment & Network Diagrams

## 1. Complete Architecture Diagram

```
┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
┃                           INTERNET (PUBLIC)                            ┃
┃                                                                         ┃
┃              Users accessing https://hrakleio-personal-training.gr     ┃
┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛
                                    ▲
                                    │ (HTTPS port 443)
                                    │ (Auto redirect from HTTP port 80)
                                    │
         ┌──────────────────────────┴──────────────────────────┐
         │                                                      │
         │            RENDER.COM INFRASTRUCTURE                │
         │                                                      │
         ├──────────────────────────────────────────────────────┤
         │                                                      │
         │  ┌────────────────────────────────────────────┐    │
         │  │  Frontend/Nginx Service (Render)          │    │
         │  │  ┌──────────────────────────────────────┐ │    │
         │  │  │ Nginx Container                      │ │    │
         │  │  │  - Port 80 (internal) → 443 (public) │ │    │
         │  │  │  - Serves static files (HTML/CSS/JS) │ │    │
         │  │  │  - Proxies /api/* requests           │ │    │
         │  │  │  - Request logging                   │ │    │
         │  │  │ Dockerfile: Dockerfile.nginx.prod    │ │    │
         │  │  └──────────────────────────────────────┘ │    │
         │  │  Runtime: ~30 seconds startup              │    │
         │  │  Public URL: gym-frontend.onrender.com     │    │
         │  └────────────────────────────────────────────┘    │
         │                      │                              │
         │                      │ (proxy /api/* to)            │
         │                      │ http://gym-backend:8080     │
         │                      ▼                              │
         │  ┌────────────────────────────────────────────┐    │
         │  │  Backend Service (Render)                 │    │
         │  │  ┌──────────────────────────────────────┐ │    │
         │  │  │ Spring Boot Container (Java 17)      │ │    │
         │  │  │  - Port 8080 (internal only)         │ │    │
         │  │  │  - Spring Boot REST API              │ │    │
         │  │  │  - Processes business logic          │ │    │
         │  │  │  - JPA/Hibernate ORM                 │ │    │
         │  │  │ Dockerfile: backend/Dockerfile.prod  │ │    │
         │  │  └──────────────────────────────────────┘ │    │
         │  │  Runtime: ~2-3 minutes startup             │    │
         │  │  Internal URL: gym-backend:8080            │    │
         │  │  NOT publicly accessible ✓                 │    │
         │  └────────────────────────────────────────────┘    │
         │                      │                              │
         │                      │ (connects to)               │
         │                      │ jdbc:mysql://host:3306     │
         │                      ▼                              │
         │  ┌────────────────────────────────────────────┐    │
         │  │  MySQL Database Service (Render)          │    │
         │  │  ┌──────────────────────────────────────┐ │    │
         │  │  │ MySQL 8.0 Container                 │ │    │
         │  │  │  - Port 3306 (internal only)        │ │    │
         │  │  │  - Database: gym                    │ │    │
         │  │  │  - Stores user & session data       │ │    │
         │  │  │  - Auto-backups (daily)             │ │    │
         │  │  └──────────────────────────────────────┘ │    │
         │  │  Internal URL: (from Render console)      │    │
         │  │  NOT publicly accessible ✓                │    │
         │  │  Backups: Automatic + Manual              │    │
         │  └────────────────────────────────────────────┘    │
         │                                                      │
         │           [Internal Network - Private]             │
         │           (Render private data center)             │
         │                                                      │
         └──────────────────────────────────────────────────────┘
```

---

## 2. Request Flow Diagram

### User Signup Flow

```
1. USER INTERACTION
   ┌─────────────────┐
   │  Browser opens  │
   │ signup.html     │
   └────────┬────────┘
            │ Nginx serves frontend
            ▼
   ┌─────────────────────────────────────┐
   │ Frontend displays signup form        │
   │ (HTML/CSS/JS from Nginx)            │
   └────────┬────────────────────────────┘
            │ User fills form & clicks submit
            │
2. API CALL
            ▼
   ┌─────────────────────────────────────────────────┐
   │ Browser JavaScript executes:                    │
   │ fetch('/api/users/signup', {POST data})         │
   └────────┬────────────────────────────────────────┘
            │ Browser sends request to /api/users/signup
            │ (HTTPS - port 443)
            ▼
   ┌─────────────────────────────────────────────────┐
   │ Nginx receives request                          │
   │ Rule: location /api/                            │
   │ Proxy to: http://backend:8080/api/users/signup  │
   └────────┬────────────────────────────────────────┘
            │ Internal HTTP to backend
            │ (Render private network)
            ▼
   ┌─────────────────────────────────────────────────┐
   │ Spring Boot Backend receives request            │
   │ UserController.signup() method                  │
   └────────┬────────────────────────────────────────┘
            │ Validates input
            │ Encrypts password
            │ Prepares SQL INSERT
            ▼
   ┌─────────────────────────────────────────────────┐
   │ Backend connects to MySQL Database              │
   │ Connection: jdbc:mysql://host:3306/gym          │
   │ Authentication: username/password               │
   │ (Internal network - private)                    │
   └────────┬────────────────────────────────────────┘
            │ Execute: INSERT INTO users (...)
            ▼
   ┌─────────────────────────────────────────────────┐
   │ MySQL receives insert                           │
   │ Stores user record                              │
   │ Returns: success/error                          │
   └────────┬────────────────────────────────────────┘
            │ Database response
            ▼
   ┌─────────────────────────────────────────────────┐
   │ Backend processes response                      │
   │ Returns JSON: {id, name, email, success:true}   │
   └────────┬────────────────────────────────────────┘
            │ JSON response via HTTP
            ▼
   ┌─────────────────────────────────────────────────┐
   │ Nginx forwards response to Browser              │
   │ (HTTPS - port 443)                              │
   └────────┬────────────────────────────────────────┘
            │ Browser JavaScript receives JSON
            ▼
   ┌─────────────────────────────────────────────────┐
   │ JavaScript processes response                   │
   │ Displays success message                        │
   │ Redirects to signin page                        │
   └─────────────────────────────────────────────────┘

Total time: ~500ms (typical)
```

---

## 3. Network Segmentation & Firewall

```
┌─────────────────────────────────────────────────────────────┐
│                    INTERNET / PUBLIC NETWORK                │
│                       (Untrusted)                           │
└────────────────────────┬──────────────────────────────────────┘
                         │
            ┌────────────┴─────────────┐
            │ (Public Access Control)  │
            │ - SSL/TLS Encryption     │
            │ - Port 443 only          │
            │ - Firewall rule: ALLOW   │
            ▼
  ┌──────────────────────────────┐
  │   FRONTEND / NGINX SERVICE   │  ◄─ PUBLICLY ACCESSIBLE ✓
  │   - Serves static files      │
  │   - HTTP port 80  ───────┐   │
  │   - HTTPS port 443       │   │
  │   - Receives from: ANY   │   │
  │   - Sends to: Backend    │   │
  └────────────┬─────────────┘   │
               │                  │
  ┌────────────┴─────────────┐   │
  │ (Internal Network        │   │
  │  Access Control)         │   │
  │ - Private IP range       │   │
  │ - No public internet     │   │
  │ - Service names DNS      │   │
  │ - Firewall: Internal     │   │
  │   connections only       │   │
  │                          │   │
  ├──────────────────────────┤   │
  │ (Firewall Rules)         │   │
  │                          │   │
  │ Backend port 8080:       │   │
  │   ├─ FROM: Nginx    ✓    │   │
  │   ├─ FROM: Internet  ✗   │   │
  │   └─ FROM: Public    ✗   │   │
  │                          │   │
  │ MySQL port 3306:         │   │
  │   ├─ FROM: Backend   ✓   │   │
  │   ├─ FROM: Internet  ✗   │   │
  │   └─ FROM: Public    ✗   │   │
  │                          │   │
  └──────────────┬───────────┘   │
                 │                │
         ┌───────▼────────┐       │
         │                │       │
         ▼                ▼       │
  ┌────────────────┐ ┌──────────┐│
  │  BACKEND       │ │ DATABASE ││
  │  SERVICE       │ │          ││
  │  (Private)     │ │ MYSQL    ││
  │  Port 8080     │ │ (Private)││
  │  ◄─ Protected  │ │ Port3306 ││
  └────────────────┘ │ ◄─Protected
                     └──────────┘

Legend:
✓ = Allowed (green)
✗ = Blocked (red)
◄─ = Not accessible from outside
```

---

## 4. DNS & Domain Resolution

```
┌──────────────────────────────────────────────────────┐
│  Your Domain Registrar (GoDaddy, Namecheap, etc.)   │
│                                                      │
│  Domain: hrakleio-personal-training.gr              │
│                                                      │
│  DNS Records:                                       │
│  ┌────────────────────────────────────────────────┐ │
│  │ Type │ Name  │ Value             │ TTL         │ │
│  ├────────────────────────────────────────────────┤ │
│  │  A   │   @   │ 12.34.56.78       │ 3600 (1h)  │ │
│  │      │       │ (Render's IP)     │            │ │
│  ├────────────────────────────────────────────────┤ │
│  │ CNAME│  www  │ gym-frontend.     │ 3600       │ │
│  │      │       │ onrender.com      │            │ │
│  └────────────────────────────────────────────────┘ │
└──────────────────────────────────────────────────────┘
                    ▲
                    │
                    │ (User types domain)
                    │
        ┌───────────┴────────────┐
        │                        │
        ▼                        ▼
    User Browser          Local DNS Resolver
    (or Phone)
        │
        │ 1. "What is the IP for hrakleio-personal-training.gr?"
        │
        ├─────────────────────────────────────────────►
        │        DNS Query (recursive)
        │
        ├─────────────────────────────────────────────►
        │  Root nameserver → TLD (.gr) → Registrar
        │
        │◄─────────────────────────────────────────────
        │        DNS Response: 12.34.56.78
        │
        │ 2. "Connect to 12.34.56.78 (HTTPS port 443)"
        │
        ├─────────────────────────────────────────────►
        │     TCP Connection + TLS Handshake
        │
        ├─────────────────────────────────────────────►
        │        Render Load Balancer
        │
        ├─────────────────────────────────────────────►
        │        Routes to Frontend Service
        │
        ▼
    Website Loads! ✓
```

---

## 5. SSL/TLS Certificate Flow

```
DAY 1: Add Custom Domain to Frontend Service
  │
  ├─► Render detects: hrakleio-personal-training.gr added
  │
  ├─► Render initiates Let's Encrypt certificate request
  │
  ├─► Let's Encrypt: "Verify you own this domain"
  │   (Challenge: DNS verification)
  │
  ├─► Render: "Adds DNS verification record"
  │
  ├─► Let's Encrypt: "Domain verified ✓"
  │
  ├─► Render: "SSL Certificate issued!"
  │
  ├─► Render: "Certificate installed on load balancer"
  │
  └─► Website now accessible via HTTPS ✓

DAY 1-90: Certificate Valid
  │
  ├─► HTTPS connections: "Certificate is valid ✓"
  │
  └─► No security warnings in browser ✓

DAY 60-90: Renewal Check
  │
  ├─► Render: "Certificate expires in 30 days"
  │
  ├─► Let's Encrypt: "Auto-renewal initiated"
  │
  ├─► Certificate renewed automatically ✓
  │
  └─► Zero downtime ✓

Result: Your domain has valid SSL certificate
        https://hrakleio-personal-training.gr is SECURE
        Certificate auto-renews every 90 days
```

---

## 6. Deployment Timeline

```
MINUTE 0: Push Code to GitHub
  │
  └─► git push origin master
      │
      └─► GitHub: "Received push"

MINUTE 1-2: Render Detects Changes
  │
  └─► Render: "Detected push to master branch"
      │
      └─► Render: "Triggered deployment"

MINUTE 3-5: Build Docker Images
  │
  ├─► Backend build
  │   ├─► Maven: download dependencies
  │   ├─► Maven: compile Java code
  │   └─► Docker: Create image (~500MB)
  │
  └─► Frontend build
      └─► Docker: Create image (~100MB)

MINUTE 6-7: Push Images to Render Registry
  │
  ├─► Backend image: uploaded
  │
  └─► Frontend image: uploaded

MINUTE 8-10: Start Containers
  │
  ├─► MySQL Database: "Already running (no restart)"
  │
  ├─► Backend Container: "Starting..."
  │   ├─► JVM warmup: ~60 seconds
  │   ├─► Database connection: verified
  │   └─► Ready on port 8080 ✓
  │
  └─► Frontend Container: "Starting..."
      ├─► Nginx loads: ~5 seconds
      └─► Ready on port 80/443 ✓

MINUTE 11: Health Checks Pass
  │
  ├─► Render: "Backend health check: OK"
  │
  ├─► Render: "Frontend health check: OK"
  │
  └─► Deployment COMPLETE ✓

MINUTE 12: Live!
  │
  └─► https://hrakleio-personal-training.gr
      Users access new version ✓

Total: ~12 minutes from push to live
```

---

## 7. Error Diagnosis Flowchart

```
                        ┌─────────────────┐
                        │ Something       │
                        │ not working?    │
                        └────────┬────────┘
                                 │
                    ┌────────────┼────────────┐
                    │            │            │
                    ▼            ▼            ▼
              Can access    Frontend       API endpoint
              frontend?     loads but       not working?
                │           API fails?
                │                │            │
         YES◄───┴──────┐    YES◄─┴──────┐    │
         │             │                 │    │
         ▼             ▼                 ▼    ▼
      ┌───────┐  ┌──────────┐  ┌──────────────────┐
      │Backend│  │ CORS     │  │Backend service   │
      │ issue │  │ issue    │  │ not running      │
      │       │  │          │  │                  │
      │Check: │  │Check:    │  │Check:            │
      │✓DNS   │  │✓Headers  │  │✓Service status   │
      │✓HTTPS │  │✓allowed- │  │✓Environment vars │
      │✓cert  │  │ origins  │  │✓Logs for errors  │
      │✓LB    │  │✓allow-   │  │✓Database conn    │
      │       │  │credentials   │✓Port 8080 open  │
      └───────┘  │          │  └──────────────────┘
                 └──────────┘

GENERAL DEBUGGING STEPS:

1. Check Render Dashboard
   └─► Service status: Running or Crashed?
   └─► View Logs: Any error messages?

2. Check Environment Variables
   └─► All set correctly?
   └─► Database credentials correct?

3. Test Individual Services
   └─► Can you reach frontend? (HTTPS works?)
   └─► Can backend reach database? (Logs show connection?)

4. Check Domain/DNS
   └─► Does DNS resolve? (nslookup, ping)
   └─► Is A record pointing to Render IP?

5. Review Recent Changes
   └─► What changed in latest commit?
   └─► Did you update API endpoint?

6. Try Manual Restart
   └─► Go to Render dashboard
   └─► Click "Manual Deploy"
   └─► Watch logs during restart
```

---

## 8. Data Flow: User Signs In

```
USER'S BROWSER                 NGINX                BACKEND              DATABASE
     │                            │                    │                    │
     │ 1. User enters email       │                    │                    │
     │    & password              │                    │                    │
     │─────────────────►          │                    │                    │
     │                            │                    │                    │
     │ 2. POST /api/users/signin  │                    │                    │
     │    (HTTPS encrypted)       │                    │                    │
     │─────────────────►          │                    │                    │
     │                            │                    │                    │
     │                   3. Verify HTTPS              │                    │
     │                   Decrypt TLS                  │                    │
     │                            │                    │                    │
     │                   4. Route /api/ to backend   │                    │
     │                   HTTP to http://backend:8080  │                    │
     │                            ├──────────────────►│                    │
     │                            │                   │                    │
     │                            │            5. Parse JSON              │
     │                            │            Call UserService.signin()   │
     │                            │                   │                    │
     │                            │            6. Query: SELECT * FROM users
     │                            │               WHERE email = ?          │
     │                            │                   ├───────────────────►│
     │                            │                   │                    │
     │                            │                   │  7. Database query │
     │                            │                   │     User found     │
     │                            │                   │◄────────────────────
     │                            │                   │                    │
     │                            │            8. Check password hash      │
     │                            │            Match? YES ✓               │
     │                            │                   │                    │
     │                            │            9. Create JWT token        │
     │                            │            Return: {id, name, token}  │
     │                            │◄──────────────────┤                    │
     │                            │                   │                    │
     │   10. Serialize JSON       │                   │                    │
     │   Set HTTPS headers        │                   │                    │
     │◄──────────────────────────│                    │                    │
     │                            │                    │                    │
     │ 11. Browser stores token  │                    │                    │
     │     in localStorage/       │                    │                    │
     │     sessionStorage         │                    │                    │
     │                            │                    │                    │
     │ 12. Redirect to           │                    │                    │
     │     /sessions.html        │                    │                    │
     └────────────────────────────────────────────────────────────────────►
                              Done! User is logged in ✓
```

---

## 9. Render.com Service Architecture

```
┌────────────────────────────────────────────────────────────┐
│                    RENDER.COM PLATFORM                    │
│                                                            │
│  ┌──────────────────────────────────────────────────────┐ │
│  │  Load Balancer (Managed by Render)                  │ │
│  │  - Handles HTTPS/TLS termination                    │ │
│  │  - Routes traffic based on domain & port            │ │
│  │  - Rate limiting & DDoS protection                  │ │
│  │  - SSL certificate management                       │ │
│  └────────┬─────────────────────────────────┬──────────┘ │
│           │                                 │              │
│     Internal Network ──────────────────────┐│              │
│           │                                ││              │
│  ┌────────▼──────────────┐        ┌───────▼─────────────┐ │
│  │  Frontend Service     │        │  Backend Service    │ │
│  │  (Nginx)              │        │  (Spring Boot)      │ │
│  │  - Public-facing      │        │  - Internal-only    │ │
│  │  - Static files       │        │  - REST APIs        │ │
│  │  - Reverse proxy      │        │  - Port 8080        │ │
│  │  - Health checks      │        │  - Auto-restart     │ │
│  │  - Logs collected     │        │  - Logs collected   │ │
│  └───────────┬───────────┘        └────────┬────────────┘ │
│              │                             │                │
│              └─────────────┬────────────────┘                │
│                            │                                │
│              ┌─────────────▼────────────────┐               │
│              │  MySQL Database Service      │               │
│              │  - Internal-only             │               │
│              │  - Port 3306                 │               │
│              │  - Persistent storage        │               │
│              │  - Automated backups         │               │
│              │  - Logs collected            │               │
│              │  - Monitoring               │               │
│              └─────────────────────────────┘               │
│                                                            │
│  ┌──────────────────────────────────────────────────────┐ │
│  │  Management & Monitoring (Render Dashboard)         │ │
│  │  - Service status                                   │ │
│  │  - Real-time logs                                  │ │
│  │  - Environment variables                          │ │
│  │  - Deployment history                             │ │
│  │  - Usage metrics                                  │ │
│  │  - Alerts & notifications                         │ │
│  └──────────────────────────────────────────────────────┘ │
│                                                            │
└────────────────────────────────────────────────────────────┘
         |  Backup to S3
         |  Monitoring
         |  Logging
         │
         └─► AWS Infrastructure (Behind the scenes)
```

---

## Summary

These diagrams show:
- ✅ How traffic flows from user to database
- ✅ Which services are public vs. private
- ✅ Where data is encrypted
- ✅ How DNS resolves your domain
- ✅ What happens during deployment
- ✅ Where to check if something breaks
- ✅ The complete system architecture

**For specific questions about each diagram, refer to the detailed documentation files.**
