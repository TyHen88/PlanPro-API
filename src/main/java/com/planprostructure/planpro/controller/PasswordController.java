package com.planprostructure.planpro.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.planprostructure.planpro.components.common.api.ProPlanRestController;
import com.planprostructure.planpro.utils.PasswordUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/wb/v1/password")
@RequiredArgsConstructor
@Tag(name = "Password", description = "Password API")
public class PasswordController extends ProPlanRestController {

    @PostMapping("/encrypt")
    public Object encryptPassword(@RequestBody @Valid String payload) throws Throwable {
        var passwordValid = payload.replace("\"", "");
        var passwordEncrypted = PasswordUtils.encrypt(passwordValid);
        return ok(passwordEncrypted);
    }
}
