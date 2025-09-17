
# Railway Deployment Checklist

## Pre-Deployment Checklist

### ✅ Code Preparation
- [ ] All code is committed to Git repository
- [ ] Application builds successfully locally
- [ ] Tests pass locally
- [ ] Production configuration files are in place

### ✅ Configuration Files
- [ ] `railway.json` - Railway deployment configuration
- [ ] `Dockerfile` - Multi-stage Docker build
- [ ] `.dockerignore` - Optimized build context
- [ ] `application-prod.yml` - Production configuration
- [ ] `application-actuator.yml` - Health check configuration
- [ ] `application-prod-migrate.yml` - Migration configuration (if needed)

### ✅ Environment Variables (Set in Railway Dashboard)
- [ ] `PROD_DB_HOST=postgres.railway.internal`
- [ ] `PROD_DB_NAME=railway`
- [ ] `PROD_DB_PASSWORD=fUqISIRtAPQPzGkzYOddXOLfiazflTZE`
- [ ] `PROD_DB_PORT=5432`
- [ ] `PROD_DB_USERNAME=postgres`
- [ ] `SPRING_PROFILES_ACTIVE=prod`
- [ ] `PORT=8080`
- [ ] `JWT_SECRET=<your-secure-jwt-secret>`
- [ ] `PASSWORD_ENCRYPTION_KEY=<your-secure-encryption-key>`
- [ ] `WEB_URL=https://your-app-name.railway.app`

### ✅ Security
- [ ] RSA keys are present in `src/main/resources/certs/`
- [ ] JWT secret is strong and unique
- [ ] Database credentials are secure
- [ ] No sensitive data in code repository

### ✅ Database
- [ ] PostgreSQL database is running on Railway
- [ ] Database credentials are correct
- [ ] Database is accessible from the application

## Deployment Steps

### 1. Initial Setup
- [ ] Create Railway account
- [ ] Connect GitHub repository
- [ ] Set up PostgreSQL database service
- [ ] Configure environment variables

### 2. First Deployment
- [ ] Set `SPRING_PROFILES_ACTIVE=prod-migrate` for schema creation
- [ ] Deploy application
- [ ] Verify database schema is created
- [ ] Change back to `SPRING_PROFILES_ACTIVE=prod`

### 3. Verification
- [ ] Application starts successfully
- [ ] Health check passes: `/actuator/health`
- [ ] API documentation accessible: `/swagger-ui.html`
- [ ] Database connection works
- [ ] File upload functionality works (if applicable)

### 4. Post-Deployment
- [ ] Test all critical API endpoints
- [ ] Verify logging is working
- [ ] Check application metrics
- [ ] Test file upload/download (if applicable)
- [ ] Verify WebSocket connections (if applicable)

## Troubleshooting

### If Deployment Fails
1. Check Railway build logs
2. Verify all environment variables are set
3. Test configuration locally using `scripts/test-prod-config.sh`
4. Check database connectivity

### If Application Won't Start
1. Check application logs in Railway dashboard
2. Verify port configuration
3. Check health endpoint
4. Verify database connection

### If Database Connection Fails
1. Verify database service is running
2. Check database credentials
3. Verify network connectivity
4. Check database logs

## Monitoring

### Health Checks
- [ ] `/actuator/health` returns 200 OK
- [ ] Database health check passes
- [ ] Disk space health check passes

### Logs
- [ ] Application logs are visible in Railway dashboard
- [ ] Error logs are properly formatted
- [ ] Log level is appropriate for production

### Metrics
- [ ] `/actuator/metrics` is accessible
- [ ] Key metrics are being collected
- [ ] Performance is within acceptable ranges

## Security Review

### Environment Variables
- [ ] No secrets in code repository
- [ ] All sensitive data in environment variables
- [ ] Strong passwords and secrets used

### Network Security
- [ ] HTTPS is enabled
- [ ] CORS is properly configured
- [ ] No unnecessary ports exposed

### Application Security
- [ ] JWT tokens are properly configured
- [ ] Password encryption is working
- [ ] File upload validation is in place

## Performance

### Database
- [ ] Connection pooling is configured
- [ ] Query performance is acceptable
- [ ] No N+1 query issues

### Application
- [ ] Response times are acceptable
- [ ] Memory usage is within limits
- [ ] CPU usage is reasonable

## Final Steps

- [ ] Update documentation with production URLs
- [ ] Set up monitoring alerts (if needed)
- [ ] Configure backup strategy
- [ ] Document deployment process
- [ ] Train team on deployment procedures

---

**Remember**: Always test in a staging environment first if possible!
