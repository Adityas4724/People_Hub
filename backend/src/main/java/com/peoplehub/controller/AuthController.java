// ============================================================
// 1. PACKAGE DECLARATION
// ============================================================
package com.peoplehub.controller;


// ============================================================
// 2. IMPORTS
// ============================================================
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.peoplehub.dto.LoginRequest;
import com.peoplehub.dto.LoginResponse;
import com.peoplehub.service.AuthService;

import jakarta.validation.Valid;


// ============================================================
// 3. REST CONTROLLER CONFIGURATION
// ============================================================
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // ========================================================
    // 4. AUTHENTICATION SERVICE DECLARATION
    // ========================================================
    private final AuthService auth;

    // ========================================================
    // 5. CONSTRUCTOR INJECTION
    // ========================================================
    public AuthController(AuthService auth) {
        this.auth = auth;
    }

    // ========================================================
    // 6. USER LOGIN API
    // ========================================================
    @PostMapping("/login")
    public LoginResponse login(
            @Valid @RequestBody LoginRequest r) {

        return auth.login(r);
    }
}