package com.planprostructure.planpro.service.auth;

import com.planprostructure.planpro.payload.auth.AuthRequest;
import com.planprostructure.planpro.payload.auth.LoginRequest;
import com.planprostructure.planpro.payload.auth.ResetPasswordRequest;
import com.planprostructure.planpro.payload.auth.SetUpPasswordRequest;
import com.planprostructure.planpro.payload.auth.UpdatePasswordRequest;

public interface AuthService {
    void register(AuthRequest request) throws Throwable;

    Object login(LoginRequest request) throws Throwable;

    void forgotPassword(String email) throws Throwable;

    void resetPassword(ResetPasswordRequest request) throws Throwable;

    Object getDebugToken(String email) throws Throwable; // DEBUG ONLY - remove in production

    Object getUserSession(String email) throws Throwable;

    void setUpPassword(SetUpPasswordRequest request) throws Throwable;

    void updatePassword(UpdatePasswordRequest request) throws Throwable;
}
