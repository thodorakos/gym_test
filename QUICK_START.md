# Quick Start: Render.com Deployment

**TL;DR** - Fast deployment path for your gym app to Render.com

---

## 1️⃣ Prepare Code (5 minutes)

```bash
cd /home/thodorakos/gym_test

# Verify .env is ignored
cat .gitignore | grep ".env"

# Create local .env (NOT committed)
cp .env.example .env
# Edit .env with your local DB values

# Commit everything
git add .
git commit -m "Production deployment files"
git push origin master
```

---

## 2️⃣ Create MySQL Database (2 minutes) - Browser

1. Go to: https://dashboard.render.com
2. Click: **"New"** → **"Database"** → **"MySQL"**
3. Fill in:
   - **Name**: `gym-db`
   - **Database name**: `gym`
   - **Region**: Europe (or closest to you)
4. Create!
5. **SAVE**: Copy the connection details (host, user, password)

---

## 3️⃣ Create Backend Service (3 minutes) - Browser

1. Click: **"New"** → **"Web Service"**
2. Select your GitHub repo
3. Fill in:
   - **Name**: `gym-backend`
   - **Region**: Same as database
   - **Runtime**: Docker
   - **Dockerfile Path**: `backend/Dockerfile.production`
4. Create!
5. Go to **Environment** section and add:

| Key | Value |
|-----|-------|
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://[INTERNAL_HOST]:3306/gym?useSSL=true&serverTimezone=UTC` |
| `SPRING_DATASOURCE_USERNAME` | From database (step 2) |
| `SPRING_DATASOURCE_PASSWORD` | From database (step 2) |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | `update` |
| `SPRING_PROFILES_ACTIVE` | `production` |

6. Deploy!

---

## 4️⃣ Create Frontend Service (3 minutes) - Browser

1. Click: **"New"** → **"Web Service"**
2. Select your GitHub repo
3. Fill in:
   - **Name**: `gym-frontend`
   - **Region**: Same as backend
   - **Runtime**: Docker
   - **Dockerfile Path**: `Dockerfile.nginx.production`
4. Create!
5. Go to **Environment** section and add:

| Key | Value |
|-----|-------|
| `BACKEND_INTERNAL_URL` | `http://gym-backend:8080` |
| `FRONTEND_API_URL` | `http://gym-backend:8080/api` |

6. Deploy!

---

## 5️⃣ Add Custom Domain (5 minutes) - Browser

1. Go to Frontend service settings
2. Find **"Custom Domains"**
3. Add: `hrakleio-personal-training.gr`
4. Render shows DNS A-record value
5. Go to your domain registrar (GoDaddy, Namecheap, etc.)
6. Update DNS A record:
   - **Host**: `@` (or root)
   - **Type**: `A`
   - **Value**: (Paste from Render)
7. Wait 5-10 minutes for DNS
8. Visit: `https://hrakleio-personal-training.gr` ✅

---

## 6️⃣ Verify Deployment (2 minutes)

```bash
# Check frontend loads
curl https://hrakleio-personal-training.gr

# Check API works
curl https://hrakleio-personal-training.gr/api/health

# Check browser console for CORS errors
# Check Render logs for any errors
```

---

## ⚠️ Critical Steps - Don't Skip!

1. **Use INTERNAL database host** in backend environment variables
2. **Set correct region** for all services (same region)
3. **Wait for services to deploy** before moving to next step
4. **Check Render logs** if something doesn't work
5. **Don't commit `.env` files** to GitHub

---

## 🔧 If Something Goes Wrong

### Backend can't connect to database
- Go to Backend service → Environment
- Verify `SPRING_DATASOURCE_URL` uses **internal** hostname (not external)
- Verify username/password match database
- Check Backend service logs

### Frontend shows "Cannot connect to API"
- Go to Frontend service → Environment
- Verify `BACKEND_INTERNAL_URL=http://gym-backend:8080`
- Check Frontend logs
- Make sure Backend service is running

### Domain not resolving
- Wait 10 minutes for DNS propagation
- Go to domain registrar and verify A record is set correctly
- Use: `nslookup hrakleio-personal-training.gr`

### Services keep restarting
- Check Render logs for errors
- Verify all required environment variables are set
- Verify Docker builds successfully locally

---

## 📊 Service URLs (After Deployment)

| Service | URL | Access |
|---------|-----|--------|
| Frontend | `https://hrakleio-personal-training.gr` | Public ✅ |
| Backend | `http://gym-backend:8080` | Internal only (Render network) |
| MySQL | Internal only | Database only |

---

## 🔐 Security Checklist

- [ ] `.env` NOT in GitHub
- [ ] Database password in environment variables (not code)
- [ ] MySQL only accessible internally
- [ ] Backend not exposed to internet
- [ ] HTTPS enabled
- [ ] No sensitive data in logs

---

## 📚 Full Documentation

For detailed guides, see:
- `RENDER_DEPLOYMENT_GUIDE.md` - Complete step-by-step guide
- `ENVIRONMENT_VARIABLES.md` - All environment variables explained
- `DEPLOYMENT_CHECKLIST.md` - Full checklist before going live

---

## Next: Update Your Frontend

After deployment, update your frontend JavaScript to use the production API:

**File**: `frontend/js/main.js` (or wherever API calls are made)

```javascript
// Change from:
const API_URL = 'http://localhost/api';

// To:
const API_URL = 'https://hrakleio-personal-training.gr/api';
// OR use relative path:
const API_URL = '/api';
```

Then commit and push - Render will auto-deploy! 🚀

---

## Cost Estimate

- Backend service: $7/month
- Frontend service: $7/month
- MySQL database: $15/month
- **Total**: ~$30/month

---

**Ready?** Start with Step 1 above! 🚀
