package com.shipcore.business.api.controller;

import com.shipcore.business.api.dto.request.LoginRequest;
import com.shipcore.business.api.dto.request.RegisterRequest;
import com.shipcore.business.api.dto.response.AuthResponse;
import com.shipcore.business.domain.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<com.shipcore.business.api.dto.response.UserResponse> getMe(
            org.springframework.security.core.Authentication authentication) {

        return ResponseEntity.ok(authService.getCurrentUser(authentication.getName()));
    }

}
