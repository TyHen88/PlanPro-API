# 🚀 Quick Start: OAuth2 Google Login

## ✅ Status: READY TO USE

---

## 🎯 Test It Now (3 Steps)

### Step 1: Start Your Application
```bash
cd /Users/henty/Documents/Coding/Ty2025/planpro_api/planpro
./gradlew bootRun
```

### Step 2: Open Your Browser
Navigate to:
```
http://localhost:8080/oauth2/authorization/google
```

### Step 3: Authorize & Get Token
1. Sign in with your Google account
2. Grant permissions
3. You'll be redirected with a JWT token
4. Copy the token from the URL

---

## 📝 Use the JWT Token

```bash
# Example: Get current user profile
curl -X GET http://localhost:8080/api/wb/v1/users/me \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

---

## 🔍 Verify in Database

```sql
-- Check users created via Google
SELECT * FROM tb_user WHERE auth_provider = 'GOOGLE';
```

---

## 🎨 Frontend Integration

```javascript
// React/JavaScript
const handleGoogleLogin = () => {
  window.location.href = 'http://localhost:8080/oauth2/authorization/google';
};

// Handle redirect on your /oauth2/redirect page
useEffect(() => {
  const token = new URLSearchParams(window.location.search).get('token');
  if (token) {
    localStorage.setItem('authToken', token);
    // Redirect to dashboard
  }
}, []);
```

---

## 🛠️ IDE Showing Errors?

The build is successful! To refresh your IDE:

**IntelliJ IDEA:**
1. Right-click on `build.gradle`
2. Select "Reimport Gradle Project" or click the Gradle refresh icon
3. Wait for indexing to complete

**VS Code:**
1. Open Command Palette (Cmd+Shift+P)
2. Type "Java: Clean Java Language Server Workspace"
3. Restart VS Code

---

## 📚 Full Documentation

- **Complete Guide:** [OAUTH2_INTEGRATION_GUIDE.md](OAUTH2_INTEGRATION_GUIDE.md)
- **Summary:** [OAUTH2_SUMMARY.md](OAUTH2_SUMMARY.md)
- **Test Page:** [oauth2-test.html](oauth2-test.html)

---

## ⚙️ Configuration Already Set

### Local Development (Ready)
✅ Google Client ID configured  
✅ Google Client Secret configured  
✅ Redirect URIs configured  
✅ Frontend redirect configured  

### Production (Need to set environment variables)
```bash
export GOOGLE_CLIENT_ID="your-production-client-id"
export GOOGLE_CLIENT_SECRET="your-production-client-secret"
export GOOGLE_REDIRECT_URI="https://yourdomain.com/login/oauth2/code/google"
export OAUTH2_REDIRECT_URI="https://yourdomain.com/oauth2/redirect"
```

---

## 🎯 Key Endpoints

| Endpoint | Purpose |
|----------|---------|
| `/oauth2/authorization/google` | Start Google login |
| `/login/oauth2/code/google` | OAuth2 callback (automatic) |
| `/api/wb/v1/users/me` | Get current user (requires JWT) |

---

## ✨ What You Get

✅ **Automatic User Registration** - New Google users are created automatically  
✅ **JWT Token Generation** - Works with your existing auth system  
✅ **Profile Sync** - Name and picture from Google  
✅ **Secure Flow** - OAuth2 authorization code flow  
✅ **Dual Auth** - LOCAL and GOOGLE login both work  

---

## 🎉 You're All Set!

Your application now supports Google OAuth2 login. Just start it and test!

```bash
./gradlew bootRun
```

Then visit: `http://localhost:8080/oauth2/authorization/google`

