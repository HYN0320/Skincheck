package com.skincheck_backend.analysis.provider;


import com.skincheck_backend.analysis.dto.AiAnalysisResponse;

public interface AiResultProvider {

    /**
     * AI 서버에 이미지 전달 → 정규화된 결과 반환
     */
    AiAnalysisResponse analyze(String imageUrl);
}