# Railway MySQL Connection Setup

## Step-by-Step Instructions

### Step 1: Connect Your Web Service to MySQL

1. Go to your **Railway project dashboard**
2. Click on your **Web Service** (your Spring Boot application)
3. Go to **"Settings"** tab
4. Scroll down to **"Service Connections"** section
5. Find your **MySQL service** in the list
6. Click **"Connect"** next to your MySQL service
   - This links the services together

### Step 2: Add Environment Variable for MySQL URL

1. Still in your **Web Service** → Go to **"Variables"** tab
2. Click **"+ New Variable"**
3. Set:
   - **Variable Name:** `SPRING_DATASOURCE_URL`
   - **Value:** `${{ MySQL.MYSQL_URL }}`
     - Click the copy icon next to `${{ MySQL.MYSQL_URL }}` in the Railway modal, or type it exactly as shown
4. Click **"Add"**

### Step 3: Set Production Profile

1. Still in **"Variables"** tab
2. Click **"+ New Variable"** (if not already set)
3. Set:
   - **Variable Name:** `SPRING_PROFILES_ACTIVE`
   - **Value:** `prod`
4. Click **"Add"**

### Step 4: Verify Environment Variables

You should now have these variables in your Web Service:
- ✅ `SPRING_DATASOURCE_URL` = `${{ MySQL.MYSQL_URL }}`
- ✅ `SPRING_PROFILES_ACTIVE` = `prod`

Railway will automatically resolve `${{ MySQL.MYSQL_URL }}` to the actual MySQL connection URL.

### Step 5: Deploy/Redeploy

1. Railway will automatically redeploy when you push code to GitHub
2. Or manually trigger: Go to **"Deployments"** → Click **"Redeploy"**

### Step 6: Check Logs

1. Go to your **Web Service** → **"Deployments"** tab
2. Click on the latest deployment
3. Click **"View Logs"**
4. Look for:
   - ✅ `HikariPool-1 - Start completed` (connection successful)
   - ✅ `Tomcat started on port(s): 8080` (app started)
   - ❌ `Connection refused` (connection failed - check steps above)

## What the Configuration Does

Your `application-prod.properties` is configured to:
1. Use `SPRING_DATASOURCE_URL` if set (Railway provides this via `${{ MySQL.MYSQL_URL }}`)
2. Fall back to other environment variables if needed
3. Explicitly set MySQL dialect to prevent errors

## Troubleshooting

### Issue: Still getting "Connection refused"
- Verify `SPRING_DATASOURCE_URL` is set to `${{ MySQL.MYSQL_URL }}` (not just `MYSQL_URL`)
- Check that services are connected (Settings → Service Connections)
- Verify MySQL service is running (green status)

### Issue: "Unable to determine Dialect"
- ✅ Already fixed! The config now explicitly sets the MySQL dialect

### Issue: Variables not showing up
- Make sure you're adding variables to the **Web Service**, not the MySQL service
- Check that services are connected first

## Success!

When it works, your app will:
- Connect to MySQL automatically
- Create tables on first run (Hibernate ddl-auto=update)
- Be accessible at your Railway URL

🎉 Your Workout Tracker is now connected to Railway MySQL!
