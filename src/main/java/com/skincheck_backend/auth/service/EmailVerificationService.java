package com.skincheck_backend.auth.service;

import com.skincheck_backend.User.entity.User;
import com.skincheck_backend.User.repository.UserRepository;
import com.skincheck_backend.common.exception.CustomException;
import com.skincheck_backend.common.mail.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final UserRepository userRepository;
    private final MailService mailService;

    /**
     * ✅ 회원가입 직후: 인증 토큰 생성 + 메일 발송
     */
    @Transactional
    public void create(User user) {
        user.createEmailVerifyToken();
        userRepository.save(user); // 🔥 반드시 저장

        String link =
                "http://localhost:8080/api/auth/verify-email?token="
                        + user.getEmailVerifyToken();

        String html = """
            <h2>SkinCheck 이메일 인증</h2>
            <p>아래 버튼을 눌러 이메일 인증을 완료해주세요.</p>
            <a href="%s"
               style="display:inline-block;
                      padding:12px 20px;
                      background:#111;
                      color:#fff;
                      text-decoration:none;
                      border-radius:8px;">
               이메일 인증하기
            </a>
            <p style="margin-top:20px;color:#666;">
               해당 링크는 24시간 동안 유효합니다.
            </p>
            """.formatted(link);

        mailService.sendEmail(
                user.getEmail(),
                "[SkinCheck] 이메일 인증을 완료해주세요",
                html
        );
    }

    /**
     * ✅ 인증 링크 클릭
     */
    @Transactional
    public void verify(String token) {

        User user = userRepository.findByEmailVerifyToken(token)
                .orElseThrow(() -> new CustomException(
                        HttpStatus.BAD_REQUEST,
                        "INVALID_TOKEN",
                        "유효하지 않은 인증 토큰입니다."
                ));

        if (user.isEmailVerified()) {
            throw new CustomException(
                    HttpStatus.BAD_REQUEST,
                    "ALREADY_VERIFIED",
                    "이미 인증된 이메일입니다."
            );
        }

        if (user.isEmailVerifyExpired()) {
            throw new CustomException(
                    HttpStatus.BAD_REQUEST,
                    "TOKEN_EXPIRED",
                    "인증 토큰이 만료되었습니다."
            );
        }

        user.verifyEmail();
        userRepository.save(user);
    }
    /**
     * ✅ 인증 메일 재전송
     */
    @Transactional
    public void resend(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(
                        HttpStatus.NOT_FOUND,
                        "USER_NOT_FOUND",
                        "존재하지 않는 이메일입니다."
                ));

        if (user.isEmailVerified()) {
            throw new CustomException(
                    HttpStatus.BAD_REQUEST,
                    "ALREADY_VERIFIED",
                    "이미 인증이 완료된 이메일입니다."
            );
        }

        // 기존 토큰 무효화 + 새 토큰 생성
        user.createEmailVerifyToken();
        userRepository.save(user);

        String link =
                "http://localhost:8080/api/auth/verify-email?token="
                        + user.getEmailVerifyToken();

        String html = """
        <h2>SkinCheck 이메일 인증</h2>
        <p>아래 버튼을 눌러 이메일 인증을 완료해주세요.</p>
        <a href="%s"
           style="display:inline-block;
                  padding:12px 20px;
                  background:#111;
                  color:#fff;
                  text-decoration:none;
                  border-radius:8px;">
           이메일 인증하기
        </a>
        <p style="margin-top:20px;color:#666;">
           본 메일은 인증 재전송 요청으로 발송되었습니다.
        </p>
        """.formatted(link);

        mailService.sendEmail(
                user.getEmail(),
                "[SkinCheck] 이메일 인증 재전송",
                html
        );
    }

}
