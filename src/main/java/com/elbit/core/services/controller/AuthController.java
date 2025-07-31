package com.elbit.core.services.controller;

import com.elbit.core.services.dto.UserRequest;
import com.elbit.core.services.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register user")
    public ResponseEntity<String> register(@RequestBody UserRequest userRequest) {
        return authService.register(userRequest);
    }

    @PostMapping("/login")
    @Operation(summary = "Login user")
    public ResponseEntity<String> login(@RequestBody UserRequest userRequest) {
        return authService.login(userRequest);
    }
}