# Railway Deployment Guide for PlanPro API

This guide will help you deploy your Spring Boot application to Railway using the provided PostgreSQL database.

## Prerequisites

1. Railway account (https://railway.app)
2. Git repository with your code
3. PostgreSQL database already set up on Railway

## Step 1: Connect Your Repository

1. Go to Railway Dashboard
2. Click "New Project"
3. Select "Deploy from GitHub repo"
4. Connect your GitHub account and select your repository

## Step 2: Configure Environment Variables

In your Railway project dashboard, go to the "Variables" tab and add the following environment variables:

### Database Configuration
```
PROD_DB_HOST=postgres.railway.internal
PROD_DB_NAME=railway
PROD_DB_PASSWORD=fUqISIRtAPQPzGkzYOddXOLfiazflTZE
PROD_DB_PORT=5432
PROD_DB_USERNAME=postgres
```

### Application Configuration
```
SPRING_PROFILES_ACTIVE=prod
PORT=8080
```

### Security Configuration
```
JWT_SECRET=your-super-secure-jwt-secret-key-here
PASSWORD_ENCRYPTION_KEY=your-super-secure-encryption-key-here
```

### Application URLs
```
WEB_URL=https://your-app-name.railway.app
```

### Optional: Telegram Configuration
```
TELEGRAM_ENABLED=false
TELEGRAM_USERNAME=planproapp_bot
TELEGRAM_ACCESS_TOKEN=7845816657:AAEJxT5tVXW88cDYELYli2ttY5glQsJQq7o
```

## Step 3: Deploy

1. Railway will automatically detect the `railway.json` configuration
2. The build process will use Gradle to build your application
3. The application will start using the production profile

## Step 4: Verify Deployment

1. Check the deployment logs in Railway dashboard
2. Visit your application URL: `https://your-app-name.railway.app`
3. Test the health endpoint: `https://your-app-name.railway.app/actuator/health`
4. Test the API documentation: `https://your-app-name.railway.app/swagger-ui.html`

## Step 5: Database Migration

If you need to run database migrations:

1. Set `SPRING_PROFILES_ACTIVE=prod-migrate` temporarily
2. Update the production configuration to use `ddl-auto: update` for the first deployment
3. After successful migration, change back to `ddl-auto: validate`

## Important Notes

### File Storage
- The application is configured to use `/tmp/upload-files/` for file storage
- This is ephemeral storage and files will be lost on container restart
- For production, consider using Railway's persistent storage or external storage (AWS S3, etc.)

### SSL Certificates
- Railway automatically provides SSL certificates
- Make sure your RSA keys are properly configured in the `src/main/resources/certs/` directory

### Monitoring
- Health checks are available at `/actuator/health`
- Metrics are available at `/actuator/metrics`
- Application info is available at `/actuator/info`

### Logs
- Application logs are available in the Railway dashboard
- Log level is set to `warn` for production to reduce noise

## Troubleshooting

### Common Issues

1. **Database Connection Failed**
   - Verify all database environment variables are set correctly
   - Check if the PostgreSQL service is running in Railway

2. **Port Issues**
   - Railway automatically sets the `PORT` environment variable
   - The application is configured to use this port

3. **Build Failures**
   - Check the build logs in Railway dashboard
   - Ensure all dependencies are properly declared in `build.gradle`

4. **Health Check Failures**
   - Verify the application is starting correctly
   - Check if the `/actuator/health` endpoint is accessible

### Useful Commands

```bash
# Check application logs
railway logs

# Check application status
railway status

# Redeploy application
railway up
```

## Security Considerations

1. **Environment Variables**: Never commit sensitive information to your repository
2. **JWT Secret**: Use a strong, unique JWT secret in production
3. **Database Password**: Keep your database credentials secure
4. **File Uploads**: Implement proper file validation and size limits
5. **CORS**: Configure CORS properly for your frontend domain

## Performance Optimization

1. **Connection Pooling**: HikariCP is configured with optimal settings for Railway
2. **JPA**: Disabled SQL logging in production for better performance
3. **Actuator**: Only essential endpoints are exposed
4. **Docker**: Multi-stage build reduces image size

## Support

If you encounter issues:
1. Check Railway documentation: https://docs.railway.app
2. Review application logs in Railway dashboard
3. Verify all environment variables are set correctly
4. Test locally with production profile before deploying
