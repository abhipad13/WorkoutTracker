# Deployment Quick Start

## 🚀 Fastest Option: Railway (5 minutes)

1. **Push to GitHub:**
   ```bash
   git init
   git add .
   git commit -m "Ready to deploy"
   git remote add origin https://github.com/YOUR_USERNAME/WorkoutTracker.git
   git push -u origin main
   ```

2. **Deploy on Railway:**
   - Go to https://railway.app
   - New Project → GitHub → Select repo
   - Add MySQL database (+ New → Database → MySQL)
   - Set environment variables:
     - `SPRING_PROFILES_ACTIVE=prod`
     - `SPRING_JPA_HIBERNATE_DDL_AUTO=update`
   - **Done!** Your app is live.

**See `DEPLOY_RAILWAY.md` for detailed steps.**

---

## 📋 Platform Comparison

| Platform | Time | Cost | Difficulty |
|----------|------|------|------------|
| **Railway** | 5 min | $5/mo | ⭐ Easiest |
| **Render** | 10 min | $7-14/mo | ⭐⭐ Easy |
| **Google Cloud** | 30 min | $10-50/mo | ⭐⭐⭐ Medium |
| **Heroku** | 15 min | $5-14/mo | ⭐⭐ Easy |

---

## ✅ Pre-Deployment Checklist

- [ ] Code pushed to GitHub
- [ ] `application-prod.properties` uses environment variables
- [ ] Database created on cloud platform
- [ ] Environment variables configured
- [ ] Port uses `${PORT:8080}`

---

## 🔧 Required Environment Variables

**Minimum required:**
```
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=<database_url>
SPRING_DATASOURCE_USERNAME=<username>
SPRING_DATASOURCE_PASSWORD=<password>
```

**Platform-specific variables are handled automatically by `application-prod.properties`**

---

## 📚 Full Guides

- **Railway:** `DEPLOY_RAILWAY.md`
- **All Platforms:** `CLOUD_DEPLOYMENT_GUIDE.md`
- **Google Cloud:** `GCP_DEPLOYMENT.md` and `QUICK_START_GCP.md`

---

## 🆘 Need Help?

1. Check platform logs
2. Verify environment variables
3. Test database connection
4. Review `CLOUD_DEPLOYMENT_GUIDE.md` troubleshooting section

**Happy deploying! 🎉**
