package com.skincheck_backend.auth.service;

import com.skincheck_backend.auth.dto.SignupRequest;
import com.skincheck_backend.common.exception.CustomException;
import com.skincheck_backend.config.security.JwtTokenProvider;
import com.skincheck_backend.User.entity.User;
import com.skincheck_backend.User.repository.UserRepository;
import com.skincheck_backend.User.entity.UserProfile;
import com.skincheck_backend.User.entity.UserSkinConcern;
import com.skincheck_backend.User.repository.UserProfileRepository;
import com.skincheck_backend.User.repository.UserSkinConcernRepository;
import com.skincheck_backend.common.enumtype.SkinConcernType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserSkinConcernRepository userSkinConcernRepository;

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * ✅ 회원가입 (기본 정보 + 추가 정보 저장)
     */
    @Transactional
    public void signup(SignupRequest req) {

        // 1️⃣ 이메일 중복 체크
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new CustomException(
                    HttpStatus.CONFLICT,
                    "EMAIL_EXISTS",
                    "이미 사용 중인 이메일입니다."
            );
        }

        // 2️⃣ User 저장 (인증 정보)
        User user = new User(
                req.getEmail(),
                passwordEncoder.encode(req.getPassword()),
                req.getName()
        );
        userRepository.save(user);

        // 3️⃣ UserProfile 저장 (추가 정보)
        UserProfile profile = new UserProfile(
                user,
                req.getGender(),
                req.getBirthYear()
        );
        userProfileRepository.save(profile);

        // 4️⃣ 피부 고민 저장 (다중 선택)
        if (req.getConcerns() != null && !req.getConcerns().isEmpty()) {
            for (SkinConcernType concern : req.getConcerns()) {
                UserSkinConcern usc = new UserSkinConcern(user, concern);
                userSkinConcernRepository.save(usc);
            }
        }
    }

    /**
     * ✅ 로그인
     */
    public String login(String email, String password) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            return jwtTokenProvider.createAccessToken(auth);
        } catch (BadCredentialsException e) {
            throw new CustomException(
                    HttpStatus.UNAUTHORIZED,
                    "BAD_CREDENTIALS",
                    "이메일 또는 비밀번호가 올바르지 않습니다."
            );
        }
    }
}
