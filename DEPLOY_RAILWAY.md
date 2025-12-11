# Deploy to Railway in 5 Minutes

Railway is the **easiest** way to deploy your Workout Tracker app.

## Prerequisites
- GitHub account
- Railway account (free at https://railway.app)

## Step-by-Step Guide

### 1. Push Your Code to GitHub

```bash
# If you haven't already initialized git
git init
git add .
git commit -m "Ready for deployment"

# Create a new repository on GitHub, then:
git remote add origin https://github.com/YOUR_USERNAME/WorkoutTracker.git
git branch -M main
git push -u origin main
```

### 2. Create Railway Project

1. Go to https://railway.app
2. Sign up/login with GitHub
3. Click **"New Project"**
4. Select **"Deploy from GitHub repo"**
5. Choose your `WorkoutTracker` repository
6. Railway will automatically detect it's a Java/Spring Boot app

### 3. Add MySQL Database

1. In your Railway project dashboard, click **"+ New"**
2. Select **"Database"** → **"MySQL"**
3. Railway will create a MySQL database automatically
4. **Note the database name** (usually `railway`)

### 4. Configure Environment Variables

Railway automatically provides database connection variables. You need to set:

1. Click on your **Web Service** (not the database)
2. Go to **"Variables"** tab
3. Add these variables (Railway provides the MySQL variables automatically):

```
SPRING_PROFILES_ACTIVE=prod
SPRING_JPA_HIBERNATE_DDL_AUTO=update
```

**That's it!** Railway automatically sets:
- `MYSQL_HOST`
- `MYSQL_PORT`
- `MYSQL_DATABASE`
- `MYSQL_USER`
- `MYSQL_PASSWORD`

Your `application-prod.properties` will automatically use these!

### 5. Deploy

Railway automatically deploys when you:
- Push to GitHub, OR
- Click **"Deploy"** in the Railway dashboard

### 6. Access Your App

Once deployed, Railway provides a URL like:
```
https://workout-tracker-production.up.railway.app
```

Click the URL or find it in your service settings under **"Domains"**.

## Custom Domain (Optional)

1. Go to your service → **"Settings"** → **"Domains"**
2. Click **"Generate Domain"** or **"Add Custom Domain"**
3. Follow the instructions to configure DNS

## Monitoring

- **Logs:** Click on your service → **"Deployments"** → Click a deployment → **"View Logs"**
- **Metrics:** View in the Railway dashboard
- **Database:** Click on MySQL service → **"Data"** tab to view tables

## Updating Your App

Simply push to GitHub:
```bash
git add .
git commit -m "Your changes"
git push
```

Railway automatically redeploys!

## Troubleshooting

### App won't start
- Check logs in Railway dashboard
- Verify environment variables are set
- Ensure database is running

### Can't connect to database
- Verify MySQL service is running
- Check that `SPRING_PROFILES_ACTIVE=prod` is set
- Review connection string in logs

### Build fails
- Check that Java 17 is specified in your Dockerfile
- Verify all dependencies in pom.xml
- Check Railway build logs

## Cost

- **Starter Plan:** $5/month (includes database)
- **Free Trial:** 14 days
- **Hobby Plan:** $20/month (more resources)

## That's It! 🎉

Your Workout Tracker is now live on the cloud!
