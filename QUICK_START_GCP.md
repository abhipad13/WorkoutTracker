# Quick Start: Deploy to Google Cloud Platform

## Quick Deployment Steps (30 minutes)

### 1. Prerequisites (5 min)
```bash
# Install Google Cloud SDK
# Download from: https://cloud.google.com/sdk/docs/install

# Login
gcloud auth login

# Create project
gcloud projects create workout-tracker-YOURNAME --name="Workout Tracker"

# Set project
gcloud config set project workout-tracker-YOURNAME

# Enable billing (required)
# Visit: https://console.cloud.google.com/billing
```

### 2. Create MySQL Database (10 min)

**Option A: Using Console (Easier)**
1. Go to: https://console.cloud.google.com/sql/instances/create
2. Select **MySQL 8.0**
3. Instance ID: `workout-db`
4. Set root password (save it!)
5. Region: `us-central1`
6. Machine: `db-f1-micro` (free tier)
7. Click **Create**

**Option B: Using Command Line**
```bash
gcloud sql instances create workout-db \
  --database-version=MYSQL_8_0 \
  --tier=db-f1-micro \
  --region=us-central1 \
  --root-password=YOUR_PASSWORD
```

**Create Database:**
```bash
gcloud sql databases create workoutdb --instance=workout-db
```

**Get Connection Name:**
```bash
gcloud sql instances describe workout-db --format="value(connectionName)"
```
Save this value! Format: `project-id:region:instance-id`

### 3. Update Configuration Files (5 min)

**Edit `src/main/resources/application-prod.properties`:**
- Replace `YOUR_PROJECT_ID` with your project ID
- Replace `YOUR_REGION` with your region (e.g., `us-central1`)
- Replace `YOUR_INSTANCE_ID` with `workout-db`

### 4. Deploy to Cloud Run (10 min)

**Build and Deploy:**
```bash
# Set variables
export PROJECT_ID=$(gcloud config get-value project)
export REGION=us-central1
export INSTANCE_ID=workout-db

# Get connection name
export CONNECTION_NAME=$(gcloud sql instances describe $INSTANCE_ID --format="value(connectionName)")

# Build container
gcloud builds submit --tag gcr.io/$PROJECT_ID/workout-tracker

# Deploy to Cloud Run
gcloud run deploy workout-tracker \
  --image gcr.io/$PROJECT_ID/workout-tracker \
  --platform managed \
  --region $REGION \
  --allow-unauthenticated \
  --add-cloudsql-instances $CONNECTION_NAME \
  --set-env-vars SPRING_PROFILES_ACTIVE=prod \
  --set-env-vars SPRING_DATASOURCE_URL="jdbc:mysql:///workoutdb?cloudSqlInstance=$CONNECTION_NAME&socketFactory=com.google.cloud.sql.mysql.SocketFactory&useSSL=false" \
  --set-env-vars SPRING_DATASOURCE_USERNAME=root \
  --set-env-vars SPRING_DATASOURCE_PASSWORD=YOUR_ROOT_PASSWORD \
  --memory 512Mi
```

### 5. Access Your App

After deployment completes, you'll get a URL like:
```
https://workout-tracker-xxx-uc.a.run.app
```

Visit it in your browser!

---

## Environment Variables Setup

Instead of hardcoding passwords, use Secret Manager:

```bash
# Create secret
echo -n "YOUR_PASSWORD" | gcloud secrets create db-password --data-file=-

# Deploy with secret
gcloud run deploy workout-tracker \
  --update-secrets=SPRING_DATASOURCE_PASSWORD=db-password:latest \
  # ... other flags
```

---

## Troubleshooting

**Can't connect to database?**
- Check Cloud SQL instance is running: `gcloud sql instances list`
- Verify connection name is correct
- Check firewall rules allow Cloud Run access

**Application won't start?**
- Check logs: `gcloud run services logs read workout-tracker`
- Verify environment variables are set correctly
- Check database password is correct

**Build fails?**
- Ensure Java 17 is in Dockerfile
- Check all dependencies in pom.xml are available
- Verify Dockerfile syntax

---

## Cost Estimate

**Free Tier Available:**
- Cloud SQL db-f1-micro: FREE (with limitations)
- Cloud Run: FREE tier includes 2 million requests/month

**Paid (Recommended):**
- Cloud SQL db-n1-standard-1: ~$25/month
- Cloud Run: Pay per use (very cheap)
- **Total: ~$25-50/month for small app**

---

## Next Steps

1. ✅ Set up custom domain
2. ✅ Enable SSL/HTTPS
3. ✅ Set up monitoring
4. ✅ Configure auto-scaling
5. ✅ Set up CI/CD pipeline

See `GCP_DEPLOYMENT.md` for detailed instructions.


