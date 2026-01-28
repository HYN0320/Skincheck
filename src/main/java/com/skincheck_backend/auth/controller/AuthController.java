package com.skincheck_backend.auth.controller;

import com.skincheck_backend.auth.dto.SignupRequest;
import com.skincheck_backend.auth.dto.LoginRequest;
import com.skincheck_backend.auth.dto.AuthResponse;
import com.skincheck_backend.auth.service.AuthService;
import com.skincheck_backend.auth.service.EmailVerificationService;
import com.skincheck_backend.User.entity.User;
import com.skincheck_backend.User.service.UserService;
import com.skincheck_backend.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final EmailVerificationService emailVerificationService; // 🔥 추가

    /**
     * ✅ 회원가입
     */
    @PostMapping("/signup")
    public ApiResponse<Void> signup(@Valid @RequestBody SignupRequest req) {
        System.out.println("🔥 SIGNUP HIT: " + req.getEmail());
        authService.signup(req);
        return ApiResponse.ok(null, "회원가입 완료");
    }

    /**
     * ✅ 로그인
     */
    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest req) {

        // 1️⃣ 로그인 (JWT 발급)
        String token = authService.login(req.getEmail(), req.getPassword());

        // 2️⃣ 사용자 조회 (name 가져오기)
        User user = userService.getByEmailOrThrow(req.getEmail());

        // 3️⃣ 토큰 + name 반환
        return ApiResponse.ok(
                AuthResponse.builder()
                        .accessToken(token)
                        .name(user.getName())
                        .build()
        );
    }

    /**
     * ✅ 이메일 인증 (링크 클릭용)
     * 예: /api/auth/verify-email?token=xxxx
     */
    @GetMapping("/verify-email")
    public ApiResponse<Void> verifyEmail(@RequestParam String token) {
        emailVerificationService.verify(token);
        return ApiResponse.ok(null, "이메일 인증이 완료되었습니다.");
    }
    @PostMapping("/resend-verify-email")
    public ApiResponse<Void> resendVerifyEmail(@RequestParam String email) {
        emailVerificationService.resend(email);
        return ApiResponse.ok(null, "인증 메일을 다시 발송했습니다.");
    }

}
