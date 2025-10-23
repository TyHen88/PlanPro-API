# 🚀 Test OAuth2 Google Login NOW!

## ✅ Everything is FIXED and READY!

---

## 🎯 3-Step Test (Takes 1 Minute)

### Step 1: Start Your App (20 seconds)
```bash
cd /Users/henty/Documents/Coding/Ty2025/planpro_api/planpro
./gradlew bootRun
```

Wait for this message:
```
Started PlanproApplication in X.XXX seconds
```

---

### Step 2: Open Browser & Login (30 seconds)

**Click or paste this URL:**
```
http://localhost:8080/oauth2/authorization/google
```

You'll see:
1. 🔄 Redirect to Google
2. 🔐 Google sign-in page
3. ✅ Grant permissions
4. 🎉 Redirect back with token!

---

### Step 3: Copy Your JWT Token (10 seconds)

After Google login, you'll be redirected to:
```
http://localhost:3000/oauth2/redirect?token=YOUR_JWT_TOKEN&type=Bearer
```

**Copy the token** from the URL!

---

## 🧪 Test the Token

```bash
# Replace YOUR_JWT_TOKEN with the actual token
curl -X GET http://localhost:8080/api/wb/v1/users/me \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

You should see your user profile! 🎉

---

## 🔍 Verify in Database

```sql
-- Check if your user was created
SELECT 
    id,
    username,
    email,
    usr_fn as first_name,
    usr_ln as last_name,
    auth_provider,
    profile_image_url,
    role,
    sts as status
FROM tb_user 
WHERE auth_provider = 'GOOGLE'
ORDER BY created_at DESC;
```

---

## ✨ What Just Happened?

When you logged in with Google:

1. ✅ You were redirected to Google's OAuth2 authorization server
2. ✅ You granted permissions to the app
3. ✅ Google sent back an authorization code
4. ✅ Your app exchanged the code for an access token
5. ✅ Your app fetched your Google profile information
6. ✅ A new user was created in your database with:
   - Your Google email
   - Your first and last name
   - Your Google profile picture
   - `auth_provider = 'GOOGLE'`
   - `role = 'USER'`
   - `status = 'ACTIVE'`
7. ✅ A JWT token was generated for you
8. ✅ You were redirected with the token

---

## 🎨 Use in Your Frontend

### React Example:
```jsx
import { useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';

function OAuth2RedirectHandler() {
  const navigate = useNavigate();
  const location = useLocation();

  useEffect(() => {
    const params = new URLSearchParams(location.search);
    const token = params.get('token');
    const error = params.get('error');

    if (token) {
      // Save token
      localStorage.setItem('authToken', token);
      localStorage.setItem('tokenType', 'Bearer');
      
      // Redirect to dashboard
      navigate('/dashboard');
    } else if (error) {
      console.error('OAuth2 Error:', error);
      navigate('/login?error=' + error);
    }
  }, [location, navigate]);

  return <div>Processing login...</div>;
}

// Login button component
function LoginButton() {
  const handleGoogleLogin = () => {
    window.location.href = 'http://localhost:8080/oauth2/authorization/google';
  };

  return (
    <button onClick={handleGoogleLogin}>
      <img src="/google-icon.svg" alt="Google" />
      Continue with Google
    </button>
  );
}
```

### Vue Example:
```vue
<template>
  <div>
    <button @click="loginWithGoogle">
      Continue with Google
    </button>
  </div>
</template>

<script>
export default {
  methods: {
    loginWithGoogle() {
      window.location.href = 'http://localhost:8080/oauth2/authorization/google';
    }
  },
  mounted() {
    // Handle OAuth2 redirect
    const urlParams = new URLSearchParams(window.location.search);
    const token = urlParams.get('token');
    
    if (token) {
      localStorage.setItem('authToken', token);
      this.$router.push('/dashboard');
    }
  }
}
</script>
```

### Angular Example:
```typescript
// login.component.ts
import { Component } from '@angular/core';

@Component({
  selector: 'app-login',
  template: `
    <button (click)="loginWithGoogle()">
      Continue with Google
    </button>
  `
})
export class LoginComponent {
  loginWithGoogle() {
    window.location.href = 'http://localhost:8080/oauth2/authorization/google';
  }
}

// oauth2-redirect.component.ts
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-oauth2-redirect',
  template: '<div>Processing login...</div>'
})
export class OAuth2RedirectComponent implements OnInit {
  constructor(
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      const token = params['token'];
      const error = params['error'];

      if (token) {
        localStorage.setItem('authToken', token);
        this.router.navigate(['/dashboard']);
      } else if (error) {
        console.error('OAuth2 Error:', error);
        this.router.navigate(['/login']);
      }
    });
  }
}
```

---

## 🔗 Alternative: Use Test HTML Page

If you don't have a frontend ready, use the provided test page:

1. **Open in browser:**
   ```
   file:///Users/henty/Documents/Coding/Ty2025/planpro_api/planpro/oauth2-test.html
   ```

2. **Click "Continue with Google"**

3. **See the results!** The page will:
   - Show your JWT token
   - Decode and display the token payload
   - Provide a cURL command to test the API

---

## 🎯 Expected Results

### After Google Login:
```
✅ Login Successful!
Token Type: Bearer

Your JWT Token:
eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJ5b3VyQGVtYWlsLmNvbSIsImlkIjoxMjMsInVzZXJuYW1lIjoieW91ckBlbWFpbC5jb20iLCJpYXQiOjE3MDk4ODAwMDAsImV4cCI6MTcwOTk2NjQwMH0...

Token Payload:
{
  "sub": "your@email.com",
  "id": 123,
  "username": "your@email.com",
  "iat": 1709880000,
  "exp": 1709966400
}
```

### In Database:
```sql
id  | username        | email           | first_name | last_name | auth_provider | role | status
----+----------------+-----------------+------------+-----------+---------------+------+--------
123 | your@email.com | your@email.com  | John       | Doe       | GOOGLE        | USER | A
```

---

## 🐛 Troubleshooting

### "redirect_uri_mismatch"
**Fix:** Make sure Google Console has:
```
http://localhost:8080/login/oauth2/code/google
```

### "Access blocked"
**Fix:** Add your email as a test user in Google Console

### "Invalid token"
**Fix:** Check RSA keys are configured in `application.yml`

### "Connection refused"
**Fix:** Make sure application is running:
```bash
./gradlew bootRun
```

---

## ✅ Success Checklist

- [ ] Application started successfully
- [ ] Visited `/oauth2/authorization/google`
- [ ] Signed in with Google
- [ ] Got redirected with JWT token
- [ ] User created in database
- [ ] Token works with protected endpoints

---

## 🎉 You're All Set!

**Start testing now:**
```bash
./gradlew bootRun
```

Then visit:
```
http://localhost:8080/oauth2/authorization/google
```

---

## 📚 More Help

- **Quick Start:** [QUICK_START_OAUTH2.md](QUICK_START_OAUTH2.md)
- **Full Guide:** [OAUTH2_INTEGRATION_GUIDE.md](OAUTH2_INTEGRATION_GUIDE.md)
- **Fix Details:** [OAUTH2_FIX_COMPLETE.md](OAUTH2_FIX_COMPLETE.md)

**Everything is ready! Happy coding! 🚀**

