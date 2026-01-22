package com.skincheck_backend.recommendation.controller;

import com.skincheck_backend.recommendation.dto.HomeResponse;
import com.skincheck_backend.recommendation.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class HomeController {

    private final HomeService homeService;

    @GetMapping("/home")
    public HomeResponse home(Authentication authentication) {

        System.out.println("🔥 [HomeController] 진입");

        if (authentication == null) {
            System.out.println("🔥 authentication = null");
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        System.out.println("🔥 user = " + authentication.getName());
        return homeService.home(authentication.getName());
    }

}
