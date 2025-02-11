package com.example.sportbet.controller;

import com.example.sportbet.dto.ApiResponse;
import com.example.sportbet.dto.response.AuthResponseDto;
import com.example.sportbet.dto.request.LoginRequestDto;
import com.example.sportbet.dto.response.UserRegistrationRequestDto;
import com.example.sportbet.model.User;
import com.example.sportbet.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> registerUser(@RequestBody UserRegistrationRequestDto dto) {
        User registeredUser = authService.registerUser(dto);
        return ResponseEntity.ok(new ApiResponse<>(true, "User registered successfully", registeredUser));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDto>> login(@RequestBody @Valid LoginRequestDto request) {
        AuthResponseDto authResponseDto = authService.login(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "User logged successfully", authResponseDto));
    }
}

