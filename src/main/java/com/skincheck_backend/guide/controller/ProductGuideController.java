package com.skincheck_backend.guide.controller;


import com.skincheck_backend.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/guide")
public class ProductGuideController {

    @GetMapping("/health")
    public ApiResponse<String> health() {
        return ApiResponse.ok("guide ok");
    }
}
