package com.planprostructure.planpro.controller;


import com.planprostructure.planpro.components.common.api.Common;
import com.planprostructure.planpro.components.common.api.ProPlanRestController;
import com.planprostructure.planpro.payload.auth.AuthRequest;
import com.planprostructure.planpro.payload.auth.LoginRequest;
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
    public ResponseEntity signup(@RequestHeader Map<String, String> headers,@Valid @RequestBody AuthRequest payload) throws Throwable{
        authService.register(payload);
        return ok(new Common(headers));
    }

    @GetMapping("/test")
    @Operation(summary = "Test Endpoint", description = "Simple test endpoint to verify API is working")
    @ApiResponse(responseCode = "200", description = "Test successful", 
                content = @Content(schema = @Schema(example = "Test successful")))
    public String test() {
        return "Test successful";
    }

    @PostMapping("/login")
    @Operation(summary = "User Login", description = "Authenticate user and return JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful", 
                    content = @Content(schema = @Schema(implementation = com.planprostructure.planpro.payload.auth.AuthResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid credentials"),
        @ApiResponse(responseCode = "401", description = "Authentication failed")
    })
    public Object login(@RequestHeader Map<String, String> headers, @RequestBody @Valid LoginRequest payload) throws Throwable{
        return ok(authService.login(payload));
    }
}
