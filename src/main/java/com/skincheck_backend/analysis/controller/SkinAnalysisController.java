package com.skincheck_backend.analysis.controller;


import com.skincheck_backend.analysis.dto.SkinAnalysisResultResponse;
import com.skincheck_backend.analysis.service.SkinAnalysisService;
import com.skincheck_backend.common.response.ApiResponse;
import com.skincheck_backend.infra.s3.S3UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/analysis")
public class SkinAnalysisController {

    private final SkinAnalysisService skinAnalysisService;
    private final S3UploadService s3UploadService;

    /**
     * 📸 이미지 업로드 → 분석
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<SkinAnalysisResultResponse> analyze(
            Authentication authentication,
            @RequestPart("image") MultipartFile image
    ) {
        String email = (authentication != null)
                ? authentication.getName()
                : null; // 익명 분석

        String imageUrl = s3UploadService.upload(image);

        return ApiResponse.ok(
                skinAnalysisService.analyzeAndSave(email, imageUrl)
        );
    }

    /**
     * 내 분석 히스토리
     */
    @GetMapping("/history")
    public ApiResponse<List<SkinAnalysisResultResponse>> myHistory(
            Authentication authentication
    ) {
        String email = authentication.getName();
        return ApiResponse.ok(
                skinAnalysisService.getMyHistory(email)
        );
    }
}
