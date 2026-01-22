package com.skincheck_backend.analysis.controller;

import com.skincheck_backend.analysis.dto.AnalysisHistoryItemResponse;
import com.skincheck_backend.analysis.dto.AnalysisInsightResponse;
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
     * 📸 이미지 업로드 → 분석 (로그인 필수)
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<SkinAnalysisResultResponse> analyze(
            Authentication authentication,
            @RequestPart("image") MultipartFile image
    ) {
        System.out.println("🔥 [Controller] 분석 요청 들어옴");

        if (authentication == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        System.out.println("🔥 [Controller] image null? " + (image == null));
        System.out.println("🔥 [Controller] image empty? " + image.isEmpty());
        System.out.println("🔥 [Controller] image size = " + image.getSize());
        System.out.println("🔥 [Controller] image contentType = " + image.getContentType());
        System.out.println("🔥 [Controller] image name = " + image.getOriginalFilename());

        String email = authentication.getName();
        System.out.println("🔥 [Controller] user email = " + email);

        // 🔴 여기서 멈추는지 확인
        System.out.println("🔥 [Controller] S3 업로드 시작");
        String imageUrl = s3UploadService.upload(image);
        System.out.println("🔥 [Controller] S3 업로드 완료: " + imageUrl);

        // 🔴 여기서 멈추는지 확인
        System.out.println("🔥 [Controller] AI 분석 시작");
        SkinAnalysisResultResponse result =
                skinAnalysisService.analyzeAndSave(email, imageUrl);
        System.out.println("🔥 [Controller] AI 분석 완료");

        return ApiResponse.ok(result);
    }

    /**
     * 내 분석 히스토리
     */
    @GetMapping("/history")
    public ApiResponse<List<AnalysisHistoryItemResponse>> myHistory(
            Authentication authentication
    ) {
        return ApiResponse.ok(
                skinAnalysisService.getMyHistory(authentication.getName())
        );
    }
    @GetMapping("/{analysisId}")
    public ApiResponse<SkinAnalysisResultResponse> getDetail(
            @PathVariable Long analysisId,
            Authentication authentication
    ) {
        return ApiResponse.ok(
                skinAnalysisService.getAnalysisDetail(
                        analysisId,
                        authentication.getName()
                )
        );
    }
    @GetMapping("/{analysisId}/insight")
    public ApiResponse<AnalysisInsightResponse> getInsight(
            @PathVariable Long analysisId,
            Authentication authentication
    ) {
        return ApiResponse.ok(
                skinAnalysisService.getInsight(
                        analysisId,
                        authentication.getName()
                )
        );
    }

}