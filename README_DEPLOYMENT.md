# 🚀 Gym Application - Render.com Deployment Guide

Complete deployment guide for deploying your gym management application to Render.com

---

## 📚 Documentation Index

### 🏃 **START HERE** - Quick Path
| Document | Purpose | Read Time |
|----------|---------|-----------|
| [**QUICK_START.md**](./QUICK_START.md) | Fast deployment in 15 minutes | 5 min |
| [**DEPLOYMENT_CHECKLIST.md**](./DEPLOYMENT_CHECKLIST.md) | Pre-flight checklist | 3 min |

### 📖 Detailed Guides
| Document | Purpose | Read Time |
|----------|---------|-----------|
| [**RENDER_DEPLOYMENT_GUIDE.md**](./RENDER_DEPLOYMENT_GUIDE.md) | Complete step-by-step with browser instructions | 20 min |
| [**ENVIRONMENT_VARIABLES.md**](./ENVIRONMENT_VARIABLES.md) | All environment variables explained | 10 min |
| [**NETWORK_FIREWALL_GUIDE.md**](./NETWORK_FIREWALL_GUIDE.md) | Network architecture & security | 15 min |
| [**VISUAL_DIAGRAMS.md**](./VISUAL_DIAGRAMS.md) | Architecture diagrams & flowcharts | 10 min |

### 🔧 Reference
| Document | Purpose |
|----------|---------|
| [**DEPLOYMENT_COMPLETE_SUMMARY.md**](./DEPLOYMENT_COMPLETE_SUMMARY.md) | Complete reference guide |
| [**.env.example**](./.env.example) | Environment variables template |

### 🐳 Docker Files
| File | Purpose |
|------|---------|
| `backend/Dockerfile.production` | Production backend build |
| `Dockerfile.nginx.production` | Production nginx image |
| `Dockerfile.production` (frontend) | Production frontend build |
| `nginx.conf.production` | Nginx reverse proxy config |
| `application-production.properties` | Spring Boot production config |
| `docker-compose.production.yml` | Local production testing |

---

## 🎯 Deployment Path

### Option 1: Fast Track (15-20 minutes)
Perfect if you just want to get it live!

```
1. Read: QUICK_START.md
2. Follow steps 1-6 (3 minutes each)
3. Done! ✓
```

### Option 2: Thorough Setup (1-2 hours)
Perfect if you want to understand everything!

```
1. Read: RENDER_DEPLOYMENT_GUIDE.md (20 min)
2. Read: NETWORK_FIREWALL_GUIDE.md (15 min)
3. Read: ENVIRONMENT_VARIABLES.md (10 min)
4. Read: VISUAL_DIAGRAMS.md (10 min)
5. Follow QUICK_START.md steps (15 min)
6. Review DEPLOYMENT_CHECKLIST.md (5 min)
```

---

## 🏗️ What's Included

### Configuration Files (For Render.com)
✅ `.env.example` - Environment variables template (commit-safe)
✅ `backend/Dockerfile.production` - Production Java build
✅ `frontend/Dockerfile.production` - Production frontend
✅ `Dockerfile.nginx.production` - Nginx container
✅ `nginx.conf.production` - Reverse proxy config
✅ `application-production.properties` - Spring Boot config
✅ `docker-compose.production.yml` - Local testing setup

### Security
✅ `.env` files ignored by `.gitignore`
✅ Database credentials via environment variables (not code)
✅ No hardcoded secrets in repository
✅ HTTPS/SSL automatic
✅ Database isolated from public internet
✅ Backend isolated from public internet

### Documentation
✅ 6 detailed guide documents
✅ Visual architecture diagrams
✅ Deployment checklist
✅ Troubleshooting guide
✅ Quick reference guide

---

## 📋 Quick Reference

### Services You'll Create on Render.com

| Service | Type | Port | Visibility | Purpose |
|---------|------|------|------------|---------|
| MySQL Database | Database | 3306 | Internal | Data storage |
| Backend API | Web Service | 8080 | Internal | REST API |
| Frontend/Nginx | Web Service | 443 | Public | Website + Proxy |

### Environment Variables to Set

**Backend Service:**
```
SPRING_DATASOURCE_URL: jdbc:mysql://[host]:3306/gym?useSSL=true&serverTimezone=UTC
SPRING_DATASOURCE_USERNAME: [from MySQL]
SPRING_DATASOURCE_PASSWORD: [from MySQL]
SPRING_JPA_HIBERNATE_DDL_AUTO: update
SPRING_PROFILES_ACTIVE: production
```

**Frontend Service:**
```
BACKEND_INTERNAL_URL: http://gym-backend:8080
FRONTEND_API_URL: http://gym-backend:8080/api
```

### Key Files to Know

| File | What | Location |
|------|------|----------|
| Dockerfile | Backend build instructions | `backend/Dockerfile.production` |
| Nginx Config | Routing & proxy rules | `nginx.conf.production` |
| App Config | Spring Boot settings | `backend/src/main/resources/application-production.properties` |
| Env Template | Variables needed | `.env.example` |

---

## 🔐 Security Checklist

Before going live:

- [ ] `.env` file NOT committed to Git
- [ ] Database credentials in environment variables
- [ ] HTTPS enabled for frontend
- [ ] Backend not publicly accessible
- [ ] Database not publicly accessible
- [ ] CORS properly configured
- [ ] No sensitive data in logs
- [ ] All environment variables set in Render

---

## 🚨 Common Issues & Quick Fixes

### "Cannot connect to database"
→ Check `SPRING_DATASOURCE_URL` uses **internal** hostname (from Render MySQL service)

### "API returning 404"
→ Verify backend service is running and environment variables are set

### "CORS error in browser"
→ Update `spring.web.cors.allowed-origins` in `application-production.properties`

### "Domain not resolving"
→ Wait 5-10 minutes for DNS propagation, then verify A record at your registrar

### "502 Bad Gateway"
→ Backend service crashed - check Render backend logs

---

## 📞 Need Help?

### Documentation
1. Check the relevant guide above
2. Search troubleshooting section in `NETWORK_FIREWALL_GUIDE.md`
3. Review `VISUAL_DIAGRAMS.md` to understand architecture

### Debugging
1. Go to Render Dashboard → Service → Logs
2. Look for error messages
3. Verify all environment variables are set
4. Check that all services are running

### External Resources
- Render Docs: https://render.com/docs
- Spring Boot: https://spring.io/projects/spring-boot
- MySQL: https://dev.mysql.com/doc/
- Nginx: https://nginx.org/

---

## 🎓 Learning Resources

### For Developers
- Understand the architecture in `VISUAL_DIAGRAMS.md`
- See network flow in `NETWORK_FIREWALL_GUIDE.md`
- Review configuration in `ENVIRONMENT_VARIABLES.md`

### For DevOps
- Study `RENDER_DEPLOYMENT_GUIDE.md` for detailed setup
- Follow `DEPLOYMENT_CHECKLIST.md` before going live
- Monitor via Render Dashboard

### For Everyone
- Start with `QUICK_START.md`
- Reference `DEPLOYMENT_COMPLETE_SUMMARY.md`

---

## 📊 Expected Costs

**Render.com Pricing:**
- Backend Web Service: $7/month (after free tier)
- Frontend Web Service: $7/month (after free tier)
- MySQL Database: $15/month
- **Total: ~$30/month**

**Domain:**
- hrakleio-personal-training.gr: Depends on registrar (~$10-15/year)

**SSL Certificate:**
- FREE (Let's Encrypt, auto-managed by Render)

---

## ✅ Deployment Steps Overview

### Step 1: Prepare Local (5 min)
- Create `.env` file locally
- Verify git has correct files
- Push to GitHub

### Step 2: Create MySQL Database (2 min)
- Render Dashboard → New → Database → MySQL
- Save connection details

### Step 3: Create Backend Service (3 min)
- Render Dashboard → New → Web Service
- Set Dockerfile: `backend/Dockerfile.production`
- Add environment variables
- Deploy

### Step 4: Create Frontend Service (3 min)
- Render Dashboard → New → Web Service
- Set Dockerfile: `Dockerfile.nginx.production`
- Add environment variables
- Deploy

### Step 5: Connect Domain (5 min)
- Frontend Settings → Custom Domains
- Add: `hrakleio-personal-training.gr`
- Update DNS at registrar
- Wait 5-10 minutes

### Step 6: Verify (5 min)
- Test: https://hrakleio-personal-training.gr
- Test: https://hrakleio-personal-training.gr/api/health
- Check browser console for errors

**Total Time: ~20-25 minutes**

---

## 🎯 Next Action

### Ready to Deploy?
→ Start with [**QUICK_START.md**](./QUICK_START.md) **RIGHT NOW!**

### Want to Learn First?
→ Start with [**VISUAL_DIAGRAMS.md**](./VISUAL_DIAGRAMS.md)

### Need Details?
→ Start with [**RENDER_DEPLOYMENT_GUIDE.md**](./RENDER_DEPLOYMENT_GUIDE.md)

---

## 📝 File Structure

```
/home/thodorakos/gym_test/
├── README.md                                    (this file)
├── QUICK_START.md                              ⭐ Start here!
├── RENDER_DEPLOYMENT_GUIDE.md                  (detailed guide)
├── ENVIRONMENT_VARIABLES.md                    (env vars reference)
├── NETWORK_FIREWALL_GUIDE.md                   (network & security)
├── VISUAL_DIAGRAMS.md                          (architecture diagrams)
├── DEPLOYMENT_CHECKLIST.md                     (pre-flight checks)
├── DEPLOYMENT_COMPLETE_SUMMARY.md              (complete reference)
├── .env.example                                (env template)
├── docker-compose.production.yml               (local testing)
├── build-production.sh                         (build script)
├── .gitignore                                  (updated for secrets)
├── .env                                        (LOCAL ONLY - not committed)
│
├── backend/
│   ├── Dockerfile.production                   (production backend)
│   ├── src/main/resources/
│   │   └── application-production.properties   (Spring Boot config)
│   └── ...
│
├── frontend/
│   ├── Dockerfile.production                   (production frontend)
│   └── ...
│
├── Dockerfile.nginx.production                 (Nginx container)
├── nginx.conf.production                       (Nginx config)
│
└── ... (other original files)
```

---

## 🔄 Continuous Deployment

After initial setup:

1. Make code changes locally
2. Commit to GitHub: `git push origin master`
3. Render automatically detects changes
4. Render rebuilds Docker images
5. Render redeploys services
6. Your site updates automatically! ✓

**No manual intervention needed after initial setup!**

---

## 🏁 Success Metrics

You'll know deployment is successful when:

✅ https://hrakleio-personal-training.gr loads
✅ Frontend displays correctly
✅ User signup/login works
✅ API calls return data
✅ No CORS errors in browser console
✅ Database stores and retrieves data
✅ No error logs in Render dashboard
✅ HTTPS certificate valid
✅ Response times are fast (<1 second)

---

## 🆘 Still Need Help?

1. **Check logs**: Render Dashboard → Service → Logs
2. **Verify setup**: Compare with `DEPLOYMENT_CHECKLIST.md`
3. **Review architecture**: Read `VISUAL_DIAGRAMS.md`
4. **Search troubleshooting**: See `NETWORK_FIREWALL_GUIDE.md`

---

## 📅 Maintenance Schedule

### Daily
- Monitor Render dashboard for errors
- Check application logs

### Weekly
- Review performance metrics
- Check for any error patterns

### Monthly
- Test backup/restore procedure
- Update dependencies (if needed)
- Review cost usage

### Quarterly
- Security audit
- Performance optimization
- Documentation updates

---

**Created: October 17, 2025**
**For: Hrakleio Personal Training (Gym Management System)**
**Domain: https://hrakleio-personal-training.gr**
**Provider: Render.com**

🚀 **Ready? Let's deploy!** → Open [QUICK_START.md](./QUICK_START.md)
