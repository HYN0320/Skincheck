package com.skincheck_backend.auth.controller;

import com.skincheck_backend.auth.dto.SignupRequest;
import com.skincheck_backend.auth.dto.LoginRequest;
import com.skincheck_backend.auth.dto.AuthResponse;
import com.skincheck_backend.auth.service.AuthService;
import com.skincheck_backend.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ApiResponse<Void> signup(@Valid @RequestBody SignupRequest req) {
        System.out.println("🔥 SIGNUP HIT: " + req.getEmail());
        authService.signup(req);
        return ApiResponse.ok(null, "회원가입 완료");
    }


    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        String token = authService.login(req.getEmail(), req.getPassword());
        return ApiResponse.ok(
                AuthResponse.builder()
                        .accessToken(token)
                        .build()
        );
    }
}
