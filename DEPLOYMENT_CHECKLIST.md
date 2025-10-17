# Deployment Checklist

## Pre-Deployment (Local)

- [ ] Code is committed to GitHub `master` branch
- [ ] `.env` file is in `.gitignore` and NOT committed
- [ ] `.env.example` is committed with placeholder values
- [ ] `Dockerfile.production` files created for backend
- [ ] `Dockerfile.nginx.production` and `nginx.conf.production` created
- [ ] `application-production.properties` configured
- [ ] All sensitive data removed from code
- [ ] CORS configuration updated for production domain
- [ ] Local testing with `docker-compose.production.yml` passes

## Render.com Setup

### 1. Render Account
- [ ] Created Render.com account
- [ ] GitHub repository connected
- [ ] GitHub OAuth configured

### 2. MySQL Database
- [ ] MySQL database created on Render
- [ ] Database name: `gym`
- [ ] Region: Europe (or preferred region)
- [ ] Connection credentials saved securely
- [ ] Internal connection URL noted
- [ ] Backups configured

### 3. Backend Service
- [ ] Web Service created
- [ ] Repository: Your GitHub repo
- [ ] Branch: `master`
- [ ] Region: Same as database
- [ ] Dockerfile: `backend/Dockerfile.production`
- [ ] Environment variables set:
  - [ ] `SPRING_DATASOURCE_URL` (using INTERNAL host)
  - [ ] `SPRING_DATASOURCE_USERNAME`
  - [ ] `SPRING_DATASOURCE_PASSWORD`
  - [ ] `SPRING_JPA_HIBERNATE_DDL_AUTO=update`
  - [ ] `SPRING_PROFILES_ACTIVE=production`
- [ ] Service deployed and running
- [ ] Logs checked for errors
- [ ] Internal URL: `http://gym-backend:8080`

### 4. Frontend Service
- [ ] Web Service created
- [ ] Repository: Your GitHub repo
- [ ] Branch: `master`
- [ ] Region: Same as backend and database
- [ ] Dockerfile: `Dockerfile.nginx.production`
- [ ] Environment variables set:
  - [ ] `BACKEND_INTERNAL_URL=http://gym-backend:8080`
  - [ ] `FRONTEND_API_URL=http://gym-backend:8080/api`
- [ ] Service deployed and running
- [ ] Logs checked for errors
- [ ] Public URL noted (e.g., gym-frontend.onrender.com)

### 5. Domain Configuration
- [ ] Custom domain added to Frontend service: `hrakleio-personal-training.gr`
- [ ] DNS A record updated at registrar
- [ ] DNS CNAME for www added (optional)
- [ ] DNS propagation verified
- [ ] SSL certificate issued (automatic)
- [ ] HTTPS access verified

## Post-Deployment Testing

### Connectivity Tests
- [ ] Frontend accessible at `https://hrakleio-personal-training.gr`
- [ ] Frontend accessible at `https://www.hrakleio-personal-training.gr` (if configured)
- [ ] API endpoint responds: `curl https://hrakleio-personal-training.gr/api/health`
- [ ] SSL certificate valid (no warnings)

### Functionality Tests
- [ ] User signup works
- [ ] User login works
- [ ] Session management works
- [ ] Database reads/writes successful
- [ ] No CORS errors in browser console
- [ ] Static files load correctly (CSS, JS, images)

### Performance & Monitoring
- [ ] Backend logs show "production" profile active
- [ ] No 404 or 500 errors in logs
- [ ] Response times acceptable
- [ ] Database queries perform well
- [ ] Memory usage stable

### Security Tests
- [ ] HTTP redirects to HTTPS
- [ ] Security headers present
- [ ] CORS properly configured
- [ ] No sensitive data in logs
- [ ] Database password not exposed in logs
- [ ] No API keys in frontend code

## Monitoring Setup
- [ ] Enable Render alerts for service restarts
- [ ] Enable database backup emails
- [ ] Check error logs weekly
- [ ] Monitor disk usage
- [ ] Monitor memory usage
- [ ] Set up uptime monitoring (optional)

## Maintenance
- [ ] Review logs weekly
- [ ] Check database size
- [ ] Verify backups are working
- [ ] Test manual deployment trigger
- [ ] Document any issues encountered
- [ ] Keep dependencies updated

## Emergency Procedures
- [ ] Document database reset procedure
- [ ] Document service restart procedure
- [ ] Have backup of database connection info
- [ ] Know how to rollback to previous version (git revert)
- [ ] Have contact info for Render support

---

## Sign-Off

- **Deployment Date**: ________________
- **Deployed By**: ________________
- **Tested By**: ________________
- **Production URL**: `https://hrakleio-personal-training.gr/`
- **Issues Encountered**: 
  - _________________________________
  - _________________________________
- **Notes**: 
  - _________________________________
  - _________________________________
