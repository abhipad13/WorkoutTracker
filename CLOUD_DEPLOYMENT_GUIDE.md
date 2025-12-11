# Easy Cloud Deployment Guide

This guide covers the **easiest ways** to deploy your Workout Tracker application and database to the cloud.

---

## 🚀 Quick Comparison: Which Platform Should I Use?

| Platform | Ease | Cost | Setup Time | Best For |
|----------|------|------|------------|----------|
| **Railway** | ⭐⭐⭐⭐⭐ | $5-20/mo | 5 min | Easiest, all-in-one |
| **Render** | ⭐⭐⭐⭐⭐ | $7-25/mo | 10 min | Simple, good free tier |
| **Google Cloud Run** | ⭐⭐⭐⭐ | $10-50/mo | 30 min | Production-ready |
| **Heroku** | ⭐⭐⭐⭐ | $7-25/mo | 15 min | Popular, well-documented |
| **AWS (Elastic Beanstalk)** | ⭐⭐⭐ | $15-50/mo | 45 min | Enterprise, scalable |

**Recommendation:** Start with **Railway** or **Render** for the easiest deployment.

---

## Option 1: Railway (Easiest - Recommended) ⭐

Railway is the **easiest** option - it handles everything automatically.

### Prerequisites
- GitHub account
- Railway account (free at https://railway.app)

### Steps (5 minutes)

#### 1. Push Code to GitHub
```bash
# Initialize git (if not already done)
git init
git add .
git commit -m "Initial commit"

# Create repository on GitHub, then:
git remote add origin https://github.com/YOUR_USERNAME/WorkoutTracker.git
git push -u origin main
```

#### 2. Connect to Railway
1. Go to https://railway.app
2. Click **"New Project"**
3. Select **"Deploy from GitHub repo"**
4. Choose your WorkoutTracker repository
5. Railway will automatically detect it's a Java/Spring Boot app

#### 3. Add MySQL Database
1. In Railway project, click **"+ New"**
2. Select **"Database"** → **"MySQL"**
3. Railway creates database automatically

#### 4. Configure Environment Variables
Railway will auto-detect your app. Add these variables:

```
SPRING_DATASOURCE_URL=jdbc:mysql://${MYSQL_HOST}:${MYSQL_PORT}/${MYSQL_DATABASE}
SPRING_DATASOURCE_USERNAME=${MYSQL_USER}
SPRING_DATASOURCE_PASSWORD=${MYSQL_PASSWORD}
SPRING_PROFILES_ACTIVE=prod
SPRING_JPA_HIBERNATE_DDL_AUTO=update
```

**Note:** Railway provides `MYSQL_HOST`, `MYSQL_PORT`, etc. automatically - just reference them!

#### 5. Deploy
Railway automatically deploys when you push to GitHub!

**That's it!** Your app will be live at `https://your-app-name.up.railway.app`

### Cost
- **Starter Plan:** $5/month (includes database)
- **Free trial:** 14 days

---

## Option 2: Render (Very Easy) ⭐

Render offers a great free tier and simple deployment.

### Steps (10 minutes)

#### 1. Push Code to GitHub
(Same as Railway - push your code to GitHub)

#### 2. Create Web Service on Render
1. Go to https://render.com
2. Click **"New +"** → **"Web Service"**
3. Connect your GitHub repository
4. Configure:
   - **Name:** `workout-tracker`
   - **Environment:** `Java`
   - **Build Command:** `./mvnw clean package -DskipTests`
   - **Start Command:** `java -jar target/WorkoutTracker-0.0.1-SNAPSHOT.jar`
   - **Plan:** Free (or Starter $7/mo)

#### 3. Add PostgreSQL Database (Render uses PostgreSQL, not MySQL)
1. Click **"New +"** → **"PostgreSQL"**
2. Name: `workout-db`
3. Plan: Free (or Starter $7/mo)

**Note:** You'll need to switch from MySQL to PostgreSQL. See "Switching to PostgreSQL" section below.

#### 4. Configure Environment Variables
In your Web Service settings, add:

```
SPRING_DATASOURCE_URL=${DATABASE_URL}
SPRING_DATASOURCE_USERNAME=${DB_USER}
SPRING_DATASOURCE_PASSWORD=${DB_PASSWORD}
SPRING_PROFILES_ACTIVE=prod
SPRING_JPA_HIBERNATE_DDL_AUTO=update
```

#### 5. Deploy
Click **"Manual Deploy"** → **"Deploy latest commit"**

**Done!** Your app will be at `https://workout-tracker.onrender.com`

### Cost
- **Free tier:** Available (with limitations)
- **Starter:** $7/month per service + $7/month for database = $14/month

---

## Option 3: Google Cloud Run (Production-Ready)

You already have detailed guides (`GCP_DEPLOYMENT.md` and `QUICK_START_GCP.md`). Here's the **simplified version**:

### Quick Steps (30 minutes)

```bash
# 1. Install gcloud CLI
# Download from: https://cloud.google.com/sdk/docs/install

# 2. Login and create project
gcloud auth login
gcloud projects create workout-tracker-YOURNAME
gcloud config set project workout-tracker-YOURNAME

# 3. Enable APIs
gcloud services enable sqladmin.googleapis.com run.googleapis.com cloudbuild.googleapis.com

# 4. Create MySQL database
gcloud sql instances create workout-db \
  --database-version=MYSQL_8_0 \
  --tier=db-f1-micro \
  --region=us-central1 \
  --root-password=YOUR_PASSWORD

gcloud sql databases create workoutdb --instance=workout-db

# 5. Get connection name
export CONNECTION_NAME=$(gcloud sql instances describe workout-db --format="value(connectionName)")

# 6. Build and deploy
gcloud builds submit --tag gcr.io/$(gcloud config get-value project)/workout-tracker

gcloud run deploy workout-tracker \
  --image gcr.io/$(gcloud config get-value project)/workout-tracker \
  --platform managed \
  --region us-central1 \
  --allow-unauthenticated \
  --add-cloudsql-instances $CONNECTION_NAME \
  --set-env-vars SPRING_PROFILES_ACTIVE=prod,SPRING_DATASOURCE_URL="jdbc:mysql:///workoutdb?cloudSqlInstance=$CONNECTION_NAME&socketFactory=com.google.cloud.sql.mysql.SocketFactory&useSSL=false",SPRING_DATASOURCE_USERNAME=root,SPRING_DATASOURCE_PASSWORD=YOUR_PASSWORD \
  --memory 512Mi
```

**See `GCP_DEPLOYMENT.md` for detailed instructions.**

---

## Option 4: Heroku (Popular Choice)

Heroku is well-documented and has a large community.

### Steps (15 minutes)

#### 1. Install Heroku CLI
```bash
# macOS
brew tap heroku/brew && brew install heroku

# Or download from: https://devcenter.heroku.com/articles/heroku-cli
```

#### 2. Login and Create App
```bash
heroku login
heroku create workout-tracker-YOURNAME
```

#### 3. Add MySQL Database
```bash
heroku addons:create cleardb:ignite
```

#### 4. Get Database URL
```bash
heroku config:get CLEARDB_DATABASE_URL
```

#### 5. Configure Environment Variables
```bash
heroku config:set SPRING_PROFILES_ACTIVE=prod
heroku config:set SPRING_JPA_HIBERNATE_DDL_AUTO=update
```

#### 6. Update application.properties
Heroku provides `CLEARDB_DATABASE_URL` automatically. Update your `application-prod.properties`:

```properties
spring.datasource.url=${CLEARDB_DATABASE_URL}
spring.jpa.hibernate.ddl-auto=update
```

#### 7. Deploy
```bash
git push heroku main
```

**Done!** Your app is at `https://workout-tracker-YOURNAME.herokuapp.com`

### Cost
- **Eco Dyno:** $5/month
- **ClearDB Ignite:** $0/month (free tier available)

---

## Option 5: AWS Elastic Beanstalk (Enterprise)

More complex but very scalable.

### Quick Steps (45 minutes)

#### 1. Install AWS CLI
```bash
# Download from: https://aws.amazon.com/cli/
aws configure
```

#### 2. Create RDS MySQL Database
```bash
aws rds create-db-instance \
  --db-instance-identifier workout-db \
  --db-instance-class db.t3.micro \
  --engine mysql \
  --master-username admin \
  --master-user-password YOUR_PASSWORD \
  --allocated-storage 20
```

#### 3. Create Elastic Beanstalk Application
```bash
# Install EB CLI
pip install awsebcli

# Initialize
eb init -p "Java 17" workout-tracker

# Create environment
eb create workout-tracker-env
```

#### 4. Configure Environment Variables
```bash
eb setenv SPRING_DATASOURCE_URL="jdbc:mysql://your-rds-endpoint:3306/workoutdb" \
  SPRING_DATASOURCE_USERNAME=admin \
  SPRING_DATASOURCE_PASSWORD=YOUR_PASSWORD \
  SPRING_PROFILES_ACTIVE=prod
```

#### 5. Deploy
```bash
eb deploy
```

### Cost
- **EC2 t3.micro:** ~$10/month
- **RDS db.t3.micro:** ~$15/month
- **Total:** ~$25/month

---

## Switching from MySQL to PostgreSQL (For Render)

If you choose Render, you'll need to switch to PostgreSQL:

### 1. Update pom.xml
```xml
<!-- Remove MySQL dependency -->
<!-- <dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
</dependency> -->

<!-- Add PostgreSQL dependency -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

### 2. Update application.properties
```properties
# Change from MySQL to PostgreSQL
spring.datasource.url=jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

### 3. Update Entity Annotations (if needed)
PostgreSQL is case-sensitive. Update your entity classes:

```java
@Table(name = "workouts")  // Use lowercase
```

Most Spring Boot code works with PostgreSQL without changes!

---

## Pre-Deployment Checklist

Before deploying, make sure:

- [ ] **Environment variables** are configured (not hardcoded)
- [ ] **Database credentials** use environment variables
- [ ] **application-prod.properties** exists with production settings
- [ ] **Dockerfile** is present (for containerized deployments)
- [ ] **.gitignore** excludes sensitive files
- [ ] **Port** is configured (usually 8080 or `$PORT`)
- [ ] **JPA ddl-auto** is set to `update` (or `none` for production)

---

## Environment Variables Template

Create a `.env.example` file (don't commit actual `.env`):

```bash
# Database
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/workoutdb
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=your_password

# Application
SPRING_PROFILES_ACTIVE=prod
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_SHOW_SQL=false

# Server
SERVER_PORT=8080
```

Update `application-prod.properties`:
```properties
spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
spring.profiles.active=${SPRING_PROFILES_ACTIVE:prod}
spring.jpa.hibernate.ddl-auto=${SPRING_JPA_HIBERNATE_DDL_AUTO:update}
server.port=${PORT:8080}
```

---

## Post-Deployment Steps

### 1. Test Your Application
- Visit your app URL
- Test creating exercises
- Test creating workouts
- Test generating reports

### 2. Monitor Logs
```bash
# Railway
railway logs

# Render
# View in dashboard

# Google Cloud Run
gcloud run services logs read workout-tracker

# Heroku
heroku logs --tail
```

### 3. Set Up Custom Domain (Optional)
- Railway: Settings → Domains → Add custom domain
- Render: Settings → Custom Domains
- Cloud Run: `gcloud run domain-mappings create`
- Heroku: Settings → Domains → Add domain

### 4. Enable HTTPS
All platforms provide HTTPS automatically!

### 5. Set Up Backups
- **Railway:** Automatic backups included
- **Render:** Manual backups via dashboard
- **Cloud SQL:** Automatic daily backups
- **Heroku:** Use Heroku Postgres backups

---

## Troubleshooting Common Issues

### Issue: "Cannot connect to database"
**Solutions:**
- Verify database is running
- Check connection string format
- Verify firewall rules allow connections
- Check username/password

### Issue: "Port already in use"
**Solutions:**
- Use `$PORT` environment variable (most platforms set this)
- Update `server.port=${PORT:8080}` in application.properties

### Issue: "Out of memory"
**Solutions:**
- Increase memory allocation (Railway/Render: upgrade plan)
- Cloud Run: `--memory 1Gi`
- Optimize application (reduce dependencies)

### Issue: "Build fails"
**Solutions:**
- Check Java version (should be 17)
- Verify all dependencies in pom.xml
- Check Dockerfile syntax
- Review build logs

### Issue: "Database schema not created"
**Solutions:**
- Set `spring.jpa.hibernate.ddl-auto=update`
- Check database connection is working
- Verify JPA entities are properly annotated

---

## Cost Comparison Summary

| Platform | App Hosting | Database | Total/Month | Free Tier |
|----------|-------------|----------|-------------|-----------|
| **Railway** | $5 | Included | $5 | 14-day trial |
| **Render** | $7 | $7 | $14 | Limited free tier |
| **Google Cloud Run** | Pay-per-use | $10-50 | $10-50 | Free tier available |
| **Heroku** | $5 | $0-9 | $5-14 | Limited free tier |
| **AWS** | $10 | $15 | $25 | Free tier (12 months) |

**Recommendation for beginners:** Start with Railway ($5/month) or Render free tier.

---

## Quick Start: Railway (Recommended)

**Fastest way to deploy (5 minutes):**

1. Push code to GitHub
2. Go to https://railway.app → New Project → GitHub
3. Add MySQL database
4. Set environment variables (Railway provides them automatically)
5. **Done!**

Your app is live! 🎉

---

## Additional Resources

- **Railway Docs:** https://docs.railway.app
- **Render Docs:** https://render.com/docs
- **Google Cloud Run:** See `GCP_DEPLOYMENT.md`
- **Heroku Docs:** https://devcenter.heroku.com/articles/getting-started-with-java
- **AWS Elastic Beanstalk:** https://docs.aws.amazon.com/elasticbeanstalk/

---

## Need Help?

1. Check platform-specific documentation
2. Review application logs
3. Verify environment variables
4. Test database connection locally first
5. Check platform status pages for outages

**Happy Deploying! 🚀**
