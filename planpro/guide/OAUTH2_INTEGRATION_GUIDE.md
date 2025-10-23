# OAuth2 Google Login Integration Guide

## 🎉 Integration Status: COMPLETE ✅

Your Spring Boot application now supports Google OAuth2 login alongside the existing username/password authentication.

## 📋 What Was Done

### 1. Dependencies Added
- ✅ `spring-boot-starter-oauth2-client` added to `build.gradle`

### 2. Configuration Files Updated
- ✅ `application-local.yml` - Added Google OAuth2 client configuration
- ✅ `application-production.yml` - Added environment variable placeholders for production

### 3. New Classes Created
- ✅ `OAuth2UserInfo.java` - Abstract class for OAuth2 user information
- ✅ `GoogleOAuth2UserInfo.java` - Google-specific user information handler
- ✅ `CustomOAuth2UserService.java` - Handles OAuth2 user registration/login
- ✅ `OAuth2UserPrincipal.java` - OAuth2 user principal implementation
- ✅ `OAuth2AuthenticationSuccessHandler.java` - Handles successful OAuth2 authentication
- ✅ `OAuth2AuthenticationFailureHandler.java` - Handles failed OAuth2 authentication

### 4. Updated Existing Classes
- ✅ `SecurityConfig.java` - Added OAuth2 login configuration
- ✅ `AuthProvider.java` - Added GOOGLE enum value
- ✅ `Users.java` - Added `authProvider` field

---

## 🚀 How to Test

### Step 1: Start Your Application
```bash
cd /Users/henty/Documents/Coding/Ty2025/planpro_api/planpro
./gradlew bootRun
```

### Step 2: Initiate Google Login

**Option A: Direct Browser Test**
1. Open your browser
2. Navigate to: `http://localhost:8080/oauth2/authorization/google`
3. You'll be redirected to Google's consent screen
4. After authorization, you'll be redirected to: `http://localhost:3000/oauth2/redirect?token=YOUR_JWT_TOKEN&type=Bearer`

**Option B: Frontend Integration**
```javascript
// React example
const handleGoogleLogin = () => {
  window.location.href = 'http://localhost:8080/oauth2/authorization/google';
};

// In your OAuth2 redirect page component
useEffect(() => {
  const urlParams = new URLSearchParams(window.location.search);
  const token = urlParams.get('token');
  const error = urlParams.get('error');
  
  if (token) {
    localStorage.setItem('token', token);
    localStorage.setItem('tokenType', 'Bearer');
    navigate('/dashboard');
  } else if (error) {
    console.error('OAuth2 error:', error);
  }
}, []);
```

### Step 3: Use the JWT Token
Once you have the token, use it in subsequent API requests:

```bash
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" \
     http://localhost:8080/api/wb/v1/users/profile
```

---

## 🔐 Google OAuth2 Setup

### Getting Google OAuth2 Credentials

1. **Go to Google Cloud Console**
   - Visit: https://console.cloud.google.com/

2. **Create/Select a Project**
   - Create a new project or select an existing one

3. **Enable Google+ API**
   - Navigate to "APIs & Services" → "Library"
   - Search for "Google+ API" and enable it

4. **Create OAuth 2.0 Credentials**
   - Go to "APIs & Services" → "Credentials"
   - Click "Create Credentials" → "OAuth 2.0 Client ID"

5. **Configure Consent Screen** (if not done)
   - Fill in required information
   - Add test users if in testing mode

6. **Configure OAuth Client**
   - Application type: **Web application**
   - Name: `PlanPro App`
   - Authorized JavaScript origins:
     - `http://localhost:8080`
     - `http://localhost:3000`
   - Authorized redirect URIs:
     - `http://localhost:8080/login/oauth2/code/google`
     - (Add production URL when deploying)

7. **Copy Credentials**
   - Copy the **Client ID** and **Client Secret**
   - Update your `application-local.yml` file (already done with test credentials)

---

## ⚙️ Configuration

### Local Development (application-local.yml)
```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: 1065879052327-njgaidr15era3c3a3siptr01ldg4142f.apps.googleusercontent.com
            client-secret: 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
            scope:
              - email
              - profile
            redirect-uri: http://localhost:8080/login/oauth2/code/google
            authorization-grant-type: authorization_code
            client-name: Google

app:
  oauth2:
    authorized-redirect-uri: http://localhost:3000/oauth2/redirect
```

### Production (Environment Variables)
Set these environment variables for production:
```bash
export GOOGLE_CLIENT_ID=your-production-client-id
export GOOGLE_CLIENT_SECRET=your-production-client-secret
export GOOGLE_REDIRECT_URI=https://yourdomain.com/login/oauth2/code/google
export OAUTH2_REDIRECT_URI=https://yourdomain.com/oauth2/redirect
```

---

## 🔄 Authentication Flow

```
┌─────────┐                    ┌──────────┐                    ┌────────┐
│ Browser │                    │  Spring  │                    │ Google │
│         │                    │   Boot   │                    │        │
└────┬────┘                    └────┬─────┘                    └───┬────┘
     │                              │                              │
     │  1. Click "Login with Google"│                              │
     ├─────────────────────────────>│                              │
     │                              │                              │
     │  2. Redirect to Google Auth  │                              │
     ├──────────────────────────────┼─────────────────────────────>│
     │                              │                              │
     │  3. User enters credentials  │                              │
     │  and grants permissions      │                              │
     │<─────────────────────────────┼──────────────────────────────│
     │                              │                              │
     │  4. Google redirects with code│                             │
     ├─────────────────────────────>│                              │
     │                              │  5. Exchange code for token  │
     │                              ├─────────────────────────────>│
     │                              │<─────────────────────────────│
     │                              │  6. Get user info            │
     │                              ├─────────────────────────────>│
     │                              │<─────────────────────────────│
     │                              │                              │
     │  7. Create/update user in DB │                              │
     │                              │                              │
     │  8. Generate JWT token       │                              │
     │                              │                              │
     │  9. Redirect to frontend     │                              │
     │     with JWT token           │                              │
     │<─────────────────────────────│                              │
     │                              │                              │
```

---

## 📊 Database Schema

When a user logs in with Google, the following fields are populated in the `tb_user` table:

```sql
CREATE TABLE tb_user (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE,
    usr_fn VARCHAR(255),        -- First name from Google
    usr_ln VARCHAR(255),        -- Last name from Google
    email VARCHAR(255) UNIQUE,  -- Email from Google
    pwd VARCHAR(255),           -- NULL for OAuth users
    profile_image_url VARCHAR(255), -- Profile picture from Google
    auth_provider VARCHAR(50),  -- 'GOOGLE' for OAuth users
    role VARCHAR(50),           -- 'USER' by default
    sts CHAR(1),               -- 'A' for ACTIVE
    -- other fields...
);
```

---

## 🛡️ Security Features

### ✅ Duplicate Email Prevention
If a user tries to login with Google using an email that's already registered with LOCAL provider (username/password), the system will show an error:
> "Looks like you're signed up with LOCAL account. Please use your LOCAL account to login."

### ✅ Automatic User Registration
First-time Google users are automatically registered with:
- Email from Google account
- First name and last name from Google
- Profile picture from Google
- Username = email
- Password = NULL (no password for OAuth users)
- Role = USER
- Status = ACTIVE
- AuthProvider = GOOGLE

### ✅ Existing User Login
Returning Google users:
- Profile picture is updated if changed
- Other information remains the same
- New JWT token is generated

---

## 🧪 Testing Endpoints

### Check User After Google Login
```bash
# Get user details using JWT token
curl -X GET http://localhost:8080/api/wb/v1/users/me \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Database Query to Verify
```sql
-- Check users created via Google OAuth
SELECT id, username, email, usr_fn, usr_ln, auth_provider, profile_image_url 
FROM tb_user 
WHERE auth_provider = 'GOOGLE';
```

---

## 🐛 Troubleshooting

### Issue: "redirect_uri_mismatch" Error
**Solution:** Make sure the redirect URI in your Google Console matches exactly:
- `http://localhost:8080/login/oauth2/code/google` (for local)
- `https://yourdomain.com/login/oauth2/code/google` (for production)

### Issue: "Invalid client" Error
**Solution:** Double-check your Client ID and Client Secret in `application-local.yml`

### Issue: "Access blocked: This app's request is invalid"
**Solution:** Make sure you've:
1. Configured the OAuth consent screen
2. Added test users (if in testing mode)
3. Enabled Google+ API

### Issue: JWT Token Not Generated
**Solution:** Check your RSA keys configuration in `application.yml`. Make sure `rsa.private-key` and `rsa.public-key` are properly configured.

---

## 🔗 Important URLs

| Purpose | Local URL | Production URL |
|---------|-----------|----------------|
| Google Login | `http://localhost:8080/oauth2/authorization/google` | `https://yourdomain.com/oauth2/authorization/google` |
| OAuth Callback | `http://localhost:8080/login/oauth2/code/google` | `https://yourdomain.com/login/oauth2/code/google` |
| Frontend Redirect | `http://localhost:3000/oauth2/redirect` | `https://yourdomain.com/oauth2/redirect` |

---

## 📝 Next Steps

1. ✅ **Test Locally** - Try the Google login flow on your local machine
2. ⬜ **Update Frontend** - Add "Login with Google" button to your frontend
3. ⬜ **Test Production** - Deploy and test on your production environment
4. ⬜ **Add More Providers** - Consider adding Facebook, GitHub, etc. (following similar pattern)

---

## 💡 Additional Features You Can Add

### 1. Link Accounts
Allow users to link their Google account to an existing LOCAL account.

### 2. Account Disconnection
Let users disconnect their Google account.

### 3. Multiple OAuth Providers
Add support for Facebook, GitHub, Twitter, etc.

### 4. Remember OAuth Provider
Store the OAuth provider in a cookie/session to show the correct login button.

---

## 📚 Resources

- [Spring Security OAuth2 Login](https://docs.spring.io/spring-security/reference/servlet/oauth2/login/index.html)
- [Google OAuth2 Documentation](https://developers.google.com/identity/protocols/oauth2)
- [JWT.io](https://jwt.io/) - Decode and inspect your JWT tokens

---

## 🎯 Summary

Your application now supports:
- ✅ Traditional username/password authentication
- ✅ Google OAuth2 authentication
- ✅ JWT token generation for both methods
- ✅ Automatic user registration for new Google users
- ✅ Protection against email conflicts between auth providers
- ✅ Profile picture sync from Google

**Ready to test!** 🚀

