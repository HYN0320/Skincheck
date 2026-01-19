package com.skincheck_backend.User.service;

import com.skincheck_backend.common.exception.CustomException;
import com.skincheck_backend.User.entity.User;
import com.skincheck_backend.User.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getByEmailOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(HttpStatus.UNAUTHORIZED, "USER_NOT_FOUND", "사용자를 찾을 수 없습니다."));
    }
}
