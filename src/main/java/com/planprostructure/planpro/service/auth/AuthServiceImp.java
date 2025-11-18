package com.planprostructure.planpro.service.auth;

import com.planprostructure.planpro.helper.AuthHelper;
import com.planprostructure.planpro.components.common.api.StatusCode;
import com.planprostructure.planpro.config.JwtUtil;
import com.planprostructure.planpro.config.UserAuthenticationProvider;
import com.planprostructure.planpro.domain.security.SecurityUser;
import com.planprostructure.planpro.domain.token.UserSession;
import com.planprostructure.planpro.domain.token.UserSessionRepository;
import com.planprostructure.planpro.domain.users.UserRepository;
import com.planprostructure.planpro.domain.users.Users;
import com.planprostructure.planpro.enums.AuthProvider;
import com.planprostructure.planpro.enums.Role;
import com.planprostructure.planpro.enums.StatusUser;
import com.planprostructure.planpro.exception.BusinessException;
import com.planprostructure.planpro.payload.auth.AuthRequest;
import com.planprostructure.planpro.payload.auth.AuthResponse;
import com.planprostructure.planpro.payload.auth.LoginRequest;
import com.planprostructure.planpro.payload.auth.ResetPasswordRequest;
import com.planprostructure.planpro.payload.auth.SetUpPasswordRequest;
import com.planprostructure.planpro.payload.auth.UpdatePasswordRequest;
import com.planprostructure.planpro.service.password.PasswordEncryption;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImp implements AuthService {
    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;
    private final JwtUtil jwtUtil;
    private final UserAuthenticationProvider userAuthenticationProvider;
    private final PasswordEncryption passwordEncryption;

    @Override
    @Transactional
    public void register(AuthRequest request) throws Throwable {

        String rawPassword;
        try {
            rawPassword = passwordEncryption.getPassword(request.getPassword());
        } catch (Exception e) {
            throw new BusinessException(StatusCode.PASSWORD_MUST_BE_ENCRYPTED);
        }

        var users = Users.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .username(request.getUsername())
                .password(rawPassword)
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .role(Role.USER)
                .status(StatusUser.ACTIVE)
                .authProvider(AuthProvider.LOCAL)
                .build();
        userRepository.save(users);

    }

    @Override
    @Transactional
    public Object login(LoginRequest request) throws Throwable {

        if (request.getUsername() == null || request.getPassword() == null) {
            throw new BusinessException(StatusCode.BAD_REQUEST, "Username and password are required");
        }

        Authentication authentication = userAuthenticationProvider.authenticate(
                request.getUsername(),
                request.getPassword());

        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        if (securityUser == null) {
            throw new BusinessException(StatusCode.AUTHENTICATION_FAILED, "Authentication failed");
        }

        if (!securityUser.isEnabled()) {
            throw new BusinessException(StatusCode.USER_DISABLED, "User account is disabled");
        }

        String token = jwtUtil.doGenerateToken(securityUser);
        return new AuthResponse(
                token,
                "Bearer",
                jwtUtil.getExpireIn());
    }

    @Override
    @Transactional
    public void forgotPassword(String email) throws Throwable {
        Optional<Users> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new BusinessException(StatusCode.USER_NOT_FOUND, "User with email " + email + " not found");
        }

        Users user = userOptional.get();

        // Deactivate any existing reset tokens for this user
        userSessionRepository.deactivateAllSessionsForUser(user.getId(), LocalDateTime.now());

        // Generate reset token
        String resetToken = jwtUtil.generateResetToken(email);

        // Set token expiry to 15 minutes from now
        LocalDateTime tokenExpiry = LocalDateTime.now().plusMinutes(15);
        LocalDateTime now = LocalDateTime.now();

        // Create new UserSession for reset token
        UserSession userSession = UserSession.builder()
                .token(resetToken)
                .userId(user.getId())
                .expiresAt(tokenExpiry)
                .createdAt(now)
                .updatedAt(now)
                .isActive(true)
                .build();

        userSessionRepository.save(userSession);
        log.info("Password reset token generated for user: {}", email);

        log.info("Reset token for {}: {}", email, resetToken);
    }

    @Transactional
    public void setUpPassword(SetUpPasswordRequest request) throws Throwable {
        // Find and validate the reset token in UserSession
        Long userId = AuthHelper.getUserId();
        Optional<Users> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new BusinessException(StatusCode.USER_NOT_FOUND, "User not found");
        }
        Users user = userOptional.get();
        if (user.getPassword() != null) {
            throw new BusinessException(StatusCode.BAD_REQUEST, "Password already setup");
        }
        String encryptedPassword;
        try {
            encryptedPassword = passwordEncryption.getPassword(request.getNewPassword());
            if (!request.getConfirmPassword().equals(request.getNewPassword())) {
                throw new BusinessException(StatusCode.PASSWORD_DOES_NOT_MATCH,
                        "Password and confirm password do not match");
            }
        } catch (Exception e) {
            throw new BusinessException(StatusCode.PASSWORD_MUST_BE_ENCRYPTED);
        }
        user.setPassword(encryptedPassword);
        userRepository.save(user);
        log.info("Password setup successfully for user: {}", user.getEmail());
    }

    @Transactional
    public void updatePassword(UpdatePasswordRequest request) throws Throwable {
        // Find and validate the reset token in UserSession
        Long userId = AuthHelper.getUserId();
        Optional<Users> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new BusinessException(StatusCode.USER_NOT_FOUND, "User not found");
        }
        Users user = userOptional.get();
        if (!passwordEncryption.verifyPassword(request.getOldPassword(), user.getPassword())) {
            System.out.println("request.getOldPassword(): " + request.getOldPassword());
            System.out.println("user.getPassword(): " + user.getPassword());
            System.out.println("passwordEncryption.verifyPassword(request.getOldPassword(), user.getPassword()): "
                    + passwordEncryption.verifyPassword(request.getOldPassword(), user.getPassword()));
            throw new BusinessException(StatusCode.CURRENT_PASSWORD, "Current password is incorrect");
        }
        String encryptedPassword;
        try {
            encryptedPassword = passwordEncryption.getPassword(request.getNewPassword());
            if (!request.getConfirmPassword().equals(request.getNewPassword())) {
                throw new BusinessException(StatusCode.PASSWORD_DOES_NOT_MATCH,
                        "New password and confirm password do not match");
            }
        } catch (Exception e) {
            throw new BusinessException(StatusCode.PASSWORD_MUST_BE_ENCRYPTED);
        }
        user.setPassword(encryptedPassword);
        userRepository.save(user);
        log.info("Password updated successfully for user: {}", user.getEmail());
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) throws Throwable {
        // Find and validate the reset token in UserSession
        Optional<UserSession> sessionOptional = userSessionRepository.findValidToken(request.sessionId(),
                LocalDateTime.now());
        if (sessionOptional.isEmpty()) {
            throw new BusinessException(StatusCode.INVALID_TOKEN, "Invalid or expired reset token");
        }

        UserSession userSession = sessionOptional.get();

        // Find user by ID from session
        Optional<Users> userOptional = userRepository.findById(userSession.getUserId());
        if (userOptional.isEmpty()) {
            throw new BusinessException(StatusCode.USER_NOT_FOUND, "User not found");
        }

        Users user = userOptional.get();

        // Validate new password
        if (request.password() == null || request.password().trim().isEmpty()) {
            throw new BusinessException(StatusCode.BAD_REQUEST, "New password is required");
        }

        // Validate confirm password
        if (request.confirmPassword() == null || request.confirmPassword().trim().isEmpty()) {
            throw new BusinessException(StatusCode.BAD_REQUEST, "Confirm password is required");
        }

        // Check if passwords match (before encryption)
        if (!request.password().equals(request.confirmPassword())) {
            throw new BusinessException(StatusCode.PASSWORD_DOES_NOT_MATCH,
                    "Password and confirm password do not match");
        }

        // Encrypt the new password
        String encryptedPassword;
        try {
            encryptedPassword = passwordEncryption.getPassword(request.password());
        } catch (Exception e) {
            throw new BusinessException(StatusCode.PASSWORD_MUST_BE_ENCRYPTED);
        }

        // Update user password
        user.setPassword(encryptedPassword);
        userRepository.save(user);

        // Deactivate the reset token after successful password reset
        userSessionRepository.deactivateToken(request.sessionId(), LocalDateTime.now());

        log.info("Password reset successfully for user: {}", user.getEmail());
    }

    @Override
    public Object getDebugToken(String email) throws Throwable {
        // DEBUG ONLY - remove in production
        Optional<Users> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new BusinessException(StatusCode.USER_NOT_FOUND, "User with email " + email + " not found");
        }

        Users user = userOptional.get();

        // Find the latest active session for this user
        var activeSessions = userSessionRepository.findActiveSessionsByUserId(user.getId(), LocalDateTime.now());

        if (activeSessions.isEmpty()) {
            return new java.util.HashMap<String, Object>() {
                {
                    put("message", "No active reset tokens found for this user");
                    put("email", email);
                }
            };
        }

        UserSession latestSession = activeSessions.get(0); // Get the first (most recent) session

        return new java.util.HashMap<String, Object>() {
            {
                put("email", email);
                put("token", latestSession.getToken());
                put("expiresAt", latestSession.getExpiresAt());
                put("isActive", latestSession.isActive());
                put("createdAt", latestSession.getCreatedAt());
            }
        };
    }

    @Override
    public Object getUserSession(String email) throws Throwable {
        Optional<UserSession> sessionOptional = userSessionRepository.findActiveSessionByEmail(email,
                LocalDateTime.now());
        if (sessionOptional.isEmpty()) {
            throw new BusinessException(StatusCode.INVALID_TOKEN, "No active session found for email: " + email);
        }

        UserSession userSession = sessionOptional.get();

        // Find user by ID from session
        Optional<Users> userOptional = userRepository.findById(userSession.getUserId());
        if (userOptional.isEmpty()) {
            throw new BusinessException(StatusCode.USER_NOT_FOUND, "User not found");
        }

        Users user = userOptional.get();

        return new java.util.HashMap<String, Object>() {
            {
                put("email", email);
                put("token", userSession.getToken());
                put("userId", userSession.getUserId());
                put("expiresAt", userSession.getExpiresAt());
                put("isActive", userSession.isActive());
                put("createdAt", userSession.getCreatedAt());
                put("user", new java.util.HashMap<String, Object>() {
                    {
                        put("id", user.getId());
                        put("username", user.getUsername());
                        put("email", user.getEmail());
                    }
                });
            }
        };
    }

}
