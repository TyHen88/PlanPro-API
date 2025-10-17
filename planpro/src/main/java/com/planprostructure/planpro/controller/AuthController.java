package com.planprostructure.planpro.controller;

import com.planprostructure.planpro.components.common.api.Common;
import com.planprostructure.planpro.components.common.api.ProPlanRestController;
import com.planprostructure.planpro.payload.auth.AuthRequest;
import com.planprostructure.planpro.payload.auth.LoginRequest;
import com.planprostructure.planpro.payload.auth.ResetPasswordRequest;
import com.planprostructure.planpro.payload.auth.SetUpPasswordRequest;
import com.planprostructure.planpro.payload.auth.UpdatePasswordRequest;
import com.planprostructure.planpro.service.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/wb/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication API endpoints")
public class AuthController extends ProPlanRestController {
    private final AuthService authService;

    @PostMapping("/signup")
    @Operation(summary = "User Registration", description = "Register a new user account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "409", description = "User already exists")
    })
    public ResponseEntity signup(@RequestHeader Map<String, String> headers, @Valid @RequestBody AuthRequest payload)
            throws Throwable {
        authService.register(payload);
        return ok(new Common(headers));
    }

    @GetMapping("/test")
    @Operation(summary = "Test Endpoint", description = "Simple test endpoint to verify API is working")
    @ApiResponse(responseCode = "200", description = "Test successful", content = @Content(schema = @Schema(example = "Test successful")))
    public String test() {
        return "Test successful";
    }

    @PostMapping("/login")
    @Operation(summary = "User Login", description = "Authenticate user and return JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful", content = @Content(schema = @Schema(implementation = com.planprostructure.planpro.payload.auth.AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid credentials"),
            @ApiResponse(responseCode = "401", description = "Authentication failed")
    })
    public Object login(@RequestHeader Map<String, String> headers, @RequestBody @Valid LoginRequest payload)
            throws Throwable {
        return ok(authService.login(payload));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Forgot Password", description = "Send password reset token to user's email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset token sent successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid email address"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity forgotPassword(@RequestHeader Map<String, String> headers,
            @RequestBody @Valid Map<String, String> request) throws Throwable {
        String email = request.get("email");
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        authService.forgotPassword(email);
        return ok(new Common(headers));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset Password", description = "Reset user password using reset token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid reset token or password"),
            @ApiResponse(responseCode = "401", description = "Reset token expired or invalid")
    })
    public ResponseEntity resetPassword(@RequestHeader Map<String, String> headers,
            @RequestBody @Valid ResetPasswordRequest payload) throws Throwable {
        authService.resetPassword(payload);
        return ok(new Common(headers));
    }

    @GetMapping("/debug-token/{email}")
    @Operation(summary = "Debug Token", description = "Get the latest reset token for an email (DEBUG ONLY)")
    public ResponseEntity debugToken(@PathVariable String email) throws Throwable {
        // This is a debug endpoint - remove in production
        return ok(authService.getDebugToken(email));
    }

    @GetMapping("/get-user-session/{email}")
    @Operation(summary = "Get User Session", description = "Get user session by token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User session retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid token")
    })
    public ResponseEntity getUserSession(@PathVariable String email)
            throws Throwable {
        return ok(authService.getUserSession(email));
    }

    @PutMapping("/setup-password")
    @Operation(summary = "Setup Password", description = "Setup password for google login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password setup successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid password"),
            @ApiResponse(responseCode = "401", description = "Password setup failed")
    })
    public ResponseEntity setupPassword(@RequestHeader Map<String, String> headers,
            @RequestBody @Valid SetUpPasswordRequest payload) throws Throwable {
        authService.setUpPassword(payload);
        return ok(new Common(headers));
    }

    @PutMapping("/update-password")
    @Operation(summary = "Update Password", description = "Update password for google login and local login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid password"),
            @ApiResponse(responseCode = "401", description = "Password update failed")
    })
    public ResponseEntity updatePassword(@RequestHeader Map<String, String> headers,
            @RequestBody @Valid UpdatePasswordRequest payload) throws Throwable {
        authService.updatePassword(payload);
        return ok(new Common(headers));
    }
}
