package com.planprostructure.planpro.service.auth;

import com.planprostructure.planpro.payload.auth.AuthRequest;
import com.planprostructure.planpro.payload.auth.LoginRequest;
import com.planprostructure.planpro.payload.auth.ResetPasswordRequest;

public interface AuthService {
    void register(AuthRequest request) throws Throwable;

    Object login(LoginRequest request) throws Throwable;

    void forgotPassword(String email) throws Throwable;

    void resetPassword(ResetPasswordRequest request) throws Throwable;

    Object getDebugToken(String email) throws Throwable; // DEBUG ONLY - remove in production

    Object getUserSession(String email) throws Throwable;
}
