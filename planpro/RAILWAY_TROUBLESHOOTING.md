# Railway Snapshot & Deployment Troubleshooting

## Repository Snapshot Issues

### Common Causes and Solutions

#### 1. Repository Size Issues
**Problem**: Repository is too large for Railway's snapshot process
**Solutions**:
- Check repository size: `git count-objects -vH`
- Remove large files from history if needed
- Use `.gitignore` to exclude unnecessary files

#### 2. Git History Issues
**Problem**: Complex git history or corrupted repository
**Solutions**:
```bash
# Clean up git repository
git gc --prune=now
git repack -a -d

# Check for issues
git fsck --full
```

#### 3. Network/Connection Issues
**Problem**: Temporary network connectivity issues
**Solutions**:
- Wait 5-10 minutes and try again
- Check your internet connection
- Try from a different network if possible

#### 4. Railway Service Issues
**Problem**: Railway's snapshot service is temporarily down
**Solutions**:
- Check Railway status: https://status.railway.app
- Wait and retry later
- Contact Railway support if persistent

## Alternative Deployment Methods

### Method 1: Manual Deployment via CLI

1. **Install Railway CLI**:
```bash
npm install -g @railway/cli
```

2. **Login to Railway**:
```bash
railway login
```

3. **Initialize Railway project**:
```bash
railway init
```

4. **Deploy manually**:
```bash
railway up
```

### Method 2: GitHub Actions Integration

Create `.github/workflows/railway-deploy.yml`:

```yaml
name: Deploy to Railway

on:
  push:
    branches: [main]

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Deploy to Railway
        uses: railwayapp/cli@v1
        with:
          railway-token: ${{ secrets.RAILWAY_TOKEN }}
          service: ${{ secrets.RAILWAY_SERVICE }}
```

### Method 3: Direct Git Push

1. **Add Railway as remote**:
```bash
railway link
```

2. **Push to Railway**:
```bash
git push railway main
```

## Pre-Deployment Checklist

### Repository Health Check
```bash
# Check repository size
git count-objects -vH

# Clean up repository
git gc --prune=now

# Check for issues
git fsck --full

# Verify all files are tracked
git status
```

### File Size Check
```bash
# Find large files in repository
find . -type f -size +10M

# Check if large files are in .gitignore
ls -la | grep -E "\.(jar|war|zip|tar|gz)$"
```

### Configuration Verification
- [ ] `railway.json` is properly formatted
- [ ] `Dockerfile` exists and is valid
- [ ] All configuration files are committed
- [ ] No sensitive data in repository

## Immediate Actions to Try

### 1. Wait and Retry
- Wait 5-10 minutes
- Refresh the Railway dashboard
- Try the snapshot process again

### 2. Clean Repository
```bash
# Remove build artifacts
rm -rf build/
rm -rf .gradle/

# Clean git
git gc --prune=now

# Commit changes
git add .
git commit -m "Clean repository for Railway deployment"
git push origin main
```

### 3. Check Repository Settings
- Ensure repository is public or Railway has access
- Verify branch name (usually `main` or `master`)
- Check if repository is properly connected to Railway

### 4. Alternative Branch
- Try deploying from a different branch
- Create a clean deployment branch:
```bash
git checkout -b railway-deploy
git push origin railway-deploy
```

## Railway CLI Alternative

If the web interface continues to fail, use the CLI:

```bash
# Install Railway CLI
npm install -g @railway/cli

# Login
railway login

# Link to project
railway link

# Deploy
railway up

# Check status
railway status

# View logs
railway logs
```

## Environment Variables Setup

Even if snapshot fails, you can set up environment variables:

1. **Via Railway Dashboard**:
   - Go to your project
   - Navigate to Variables tab
   - Add all required environment variables

2. **Via Railway CLI**:
```bash
railway variables set PROD_DB_HOST=postgres.railway.internal
railway variables set PROD_DB_NAME=railway
railway variables set PROD_DB_PASSWORD=fUqISIRtAPQPzGkzYOddXOLfiazflTZE
railway variables set PROD_DB_PORT=5432
railway variables set PROD_DB_USERNAME=postgres
railway variables set SPRING_PROFILES_ACTIVE=prod
railway variables set PORT=8080
```

## Contact Support

If issues persist:

1. **Railway Support**: https://railway.app/support
2. **GitHub Issues**: Check if others are experiencing similar issues
3. **Community Discord**: Railway's community Discord for help

## Quick Fix Commands

```bash
# Quick repository cleanup
git gc --prune=now && git repack -a -d

# Check for issues
git fsck --full

# Force push if needed (be careful!)
git push origin main --force

# Alternative: Create fresh branch
git checkout -b fresh-deploy
git push origin fresh-deploy
```

## Monitoring Deployment

Once deployed, monitor:

1. **Build logs**: Check for build errors
2. **Deployment logs**: Verify application startup
3. **Health checks**: `/actuator/health`
4. **Application logs**: Monitor for runtime errors

---

**Remember**: Most snapshot issues are temporary. Try the CLI approach if the web interface continues to fail.
