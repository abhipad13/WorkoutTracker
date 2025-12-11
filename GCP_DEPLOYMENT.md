# Google Cloud Platform Deployment Guide

This guide will help you deploy your Spring Boot Workout Tracker application to Google Cloud Platform, including the MySQL database on Cloud SQL and the Spring Boot server on Cloud Run.

---

## Prerequisites

1. **Google Cloud Account** - Sign up at https://cloud.google.com
2. **Google Cloud SDK (gcloud)** - Install from https://cloud.google.com/sdk/docs/install
3. **Maven** - Already installed (you have pom.xml)
4. **Java 17** - Already installed

---

## Step 1: Set Up Google Cloud Project

### 1.1 Create a New Project
```bash
# Login to Google Cloud
gcloud auth login

# Create a new project (replace YOUR_PROJECT_ID with a unique name)
gcloud projects create workout-tracker-YOUR_PROJECT_ID

# Set as active project
gcloud config set project workout-tracker-YOUR_PROJECT_ID

# Enable billing (required for Cloud SQL)
# Go to: https://console.cloud.google.com/billing
```

### 1.2 Enable Required APIs
```bash
# Enable Cloud SQL Admin API
gcloud services enable sqladmin.googleapis.com

# Enable Cloud Run API
gcloud services enable run.googleapis.com

# Enable Cloud Build API (for building Docker images)
gcloud services enable cloudbuild.googleapis.com
```

---

## Step 2: Create Cloud SQL MySQL Instance

### 2.1 Create MySQL Instance via Console (Easier)

1. Go to: https://console.cloud.google.com/sql/instances
2. Click **"Create Instance"**
3. Choose **MySQL**
4. Select **MySQL 8.0**
5. Fill in:
   - **Instance ID**: `workout-tracker-db`
   - **Root password**: Set a strong password (save it!)
   - **Region**: Choose closest to you (e.g., `us-central1`)
   - **Zone**: Leave default
   - **Database Version**: MySQL 8.0
6. Under **Configuration Options**:
   - **Machine Type**: `db-f1-micro` (free tier) or `db-n1-standard-1` (paid)
7. Click **"Create Instance"** (takes 5-10 minutes)

### 2.2 Create Database

1. Once instance is created, go to the instance
2. Click **"Databases"** tab
3. Click **"Create Database"**
4. Name: `workoutdb`
5. Click **"Create"**

### 2.3 Create Database User (Optional but Recommended)

1. Go to **"Users"** tab
2. Click **"Add User Account"**
3. Username: `workoutuser`
4. Password: Set a password (different from root)
5. Click **"Add"**

### 2.4 Get Connection Name

1. Go to instance overview
2. Copy the **Connection name** (looks like: `project-id:region:instance-id`)
3. Save it for later!

---

## Step 3: Configure Spring Boot for Cloud SQL

### 3.1 Update application.properties

Create a new file for production: `src/main/resources/application-prod.properties`

```properties
# Production profile for Google Cloud
spring.profiles.active=prod

# Cloud SQL connection (using Unix socket)
# Replace YOUR_PROJECT_ID, YOUR_REGION, YOUR_INSTANCE_ID
spring.datasource.url=jdbc:mysql:///workoutdb?cloudSqlInstance=YOUR_PROJECT_ID:YOUR_REGION:YOUR_INSTANCE_ID&socketFactory=com.google.cloud.sql.mysql.SocketFactory&useSSL=false

# Database credentials
spring.datasource.username=root
spring.datasource.password=YOUR_ROOT_PASSWORD

# JPA settings
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=false

# Server settings
server.port=8080
```

### 3.2 Add Cloud SQL JDBC Socket Factory Dependency

Update your `pom.xml` to add Cloud SQL connector:

```xml
<dependency>
    <groupId>com.google.cloud.sql</groupId>
    <artifactId>mysql-socket-factory-connector-j-8</artifactId>
    <version>1.15.0</version>
</dependency>
```

---

## Step 4: Create Dockerfile for Cloud Run

Create `Dockerfile` in project root:

```dockerfile
# Use OpenJDK 17 as base image
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies
RUN ./mvnw dependency:go-offline

# Copy source code
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "target/WorkoutTracker-0.0.1-SNAPSHOT.jar"]
```

---

## Step 5: Build and Deploy to Cloud Run

### 5.1 Build Docker Image

```bash
# Set project
gcloud config set project workout-tracker-YOUR_PROJECT_ID

# Set default region
gcloud config set run/region us-central1

# Build using Cloud Build
gcloud builds submit --tag gcr.io/YOUR_PROJECT_ID/workout-tracker

# OR build locally (if Docker is installed)
docker build -t gcr.io/YOUR_PROJECT_ID/workout-tracker .
docker push gcr.io/YOUR_PROJECT_ID/workout-tracker
```

### 5.2 Deploy to Cloud Run

```bash
# Deploy to Cloud Run
gcloud run deploy workout-tracker \
  --image gcr.io/YOUR_PROJECT_ID/workout-tracker \
  --platform managed \
  --region us-central1 \
  --allow-unauthenticated \
  --add-cloudsql-instances YOUR_PROJECT_ID:YOUR_REGION:YOUR_INSTANCE_ID \
  --set-env-vars SPRING_PROFILES_ACTIVE=prod \
  --set-env-vars SPRING_DATASOURCE_URL="jdbc:mysql:///workoutdb?cloudSqlInstance=YOUR_PROJECT_ID:YOUR_REGION:YOUR_INSTANCE_ID&socketFactory=com.google.cloud.sql.mysql.SocketFactory&useSSL=false" \
  --set-env-vars SPRING_DATASOURCE_USERNAME=root \
  --set-env-vars SPRING_DATASOURCE_PASSWORD=YOUR_ROOT_PASSWORD \
  --memory 512Mi \
  --cpu 1
```

---

## Step 6: Alternative - Deploy to App Engine (Simpler)

### 6.1 Create app.yaml

Create `app.yaml` in project root:

```yaml
runtime: java17

env_variables:
  SPRING_PROFILES_ACTIVE: prod
  SPRING_DATASOURCE_URL: jdbc:mysql:///workoutdb?cloudSqlInstance=YOUR_PROJECT_ID:YOUR_REGION:YOUR_INSTANCE_ID&socketFactory=com.google.cloud.sql.mysql.SocketFactory&useSSL=false
  SPRING_DATASOURCE_USERNAME: root
  SPRING_DATASOURCE_PASSWORD: YOUR_ROOT_PASSWORD

beta_settings:
  cloud_sql_instances: YOUR_PROJECT_ID:YOUR_REGION:YOUR_INSTANCE_ID

automatic_scaling:
  min_instances: 1
  max_instances: 3
```

### 6.2 Deploy to App Engine

```bash
gcloud app deploy
```

---

## Step 7: Configure Cloud SQL Access

### 7.1 Allow Cloud Run/App Engine to Access Cloud SQL

The `--add-cloudsql-instances` flag (Cloud Run) or `beta_settings.cloud_sql_instances` (App Engine) automatically configures access.

### 7.2 Test Connection Locally (Optional)

```bash
# Start Cloud SQL Proxy locally
cloud_sql_proxy -instances=YOUR_PROJECT_ID:YOUR_REGION:YOUR_INSTANCE_ID=tcp:3306

# Then use local connection in application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/workoutdb
```

---

## Step 8: Environment Variables (Secure Method)

For better security, use Secret Manager instead of hardcoding passwords:

### 8.1 Create Secret

```bash
echo -n "YOUR_ROOT_PASSWORD" | gcloud secrets create db-password --data-file=-
```

### 8.2 Update Deployment

```bash
gcloud run deploy workout-tracker \
  --image gcr.io/YOUR_PROJECT_ID/workout-tracker \
  --update-secrets=SPRING_DATASOURCE_PASSWORD=db-password:latest \
  # ... other flags
```

---

## Step 9: Update Application Code (if needed)

Make sure your `application.properties` can use environment variables:

```properties
spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
```

---

## Step 10: Access Your Application

After deployment:

1. **Cloud Run**: Get URL from `gcloud run services list`
   - Format: `https://workout-tracker-xxx-uc.a.run.app`

2. **App Engine**: 
   - URL: `https://YOUR_PROJECT_ID.appspot.com`

3. Visit the URL in your browser!

---

## Quick Reference Commands

```bash
# View Cloud Run services
gcloud run services list

# View logs
gcloud run services logs read workout-tracker

# Update environment variables
gcloud run services update workout-tracker --update-env-vars KEY=VALUE

# Delete service
gcloud run services delete workout-tracker

# View Cloud SQL instances
gcloud sql instances list

# Connect to Cloud SQL (local)
gcloud sql connect YOUR_INSTANCE_ID --user=root
```

---

## Troubleshooting

### Issue: Connection refused
- **Solution**: Check Cloud SQL instance is running and connection name is correct

### Issue: Authentication failed
- **Solution**: Verify database username/password and that Cloud Run has access to Cloud SQL

### Issue: Out of memory
- **Solution**: Increase memory: `--memory 1Gi` in Cloud Run deployment

### Issue: Build fails
- **Solution**: Check Java version matches (should be 17), verify all dependencies in pom.xml

---

## Cost Estimation

### Free Tier (Cloud SQL)
- `db-f1-micro`: FREE (limited resources, suitable for testing)

### Paid (Recommended for production)
- `db-n1-standard-1`: ~$25/month
- Cloud Run: Pay per request (very cheap for low traffic)
- Cloud SQL: ~$10-50/month depending on instance size

**Total estimated cost for small app: $15-75/month**

---

## Security Best Practices

1. **Use Secret Manager** for passwords (not environment variables)
2. **Enable SSL** for database connections in production
3. **Use IAM** to control who can access Cloud SQL
4. **Set up VPC** for private IP connections (advanced)
5. **Regular backups** - Cloud SQL automatically backs up

---

## Next Steps

1. Set up custom domain (optional)
2. Configure Cloud CDN for static files
3. Set up monitoring and alerts
4. Configure auto-scaling
5. Set up CI/CD pipeline

---

## Resources

- Cloud SQL Documentation: https://cloud.google.com/sql/docs/mysql
- Cloud Run Documentation: https://cloud.google.com/run/docs
- Spring Boot on GCP: https://spring.io/guides/gs/spring-boot-for-google-cloud-platform

