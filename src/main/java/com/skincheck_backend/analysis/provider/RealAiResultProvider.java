package com.skincheck_backend.analysis.provider;

import com.skincheck_backend.analysis.dto.AiAnalysisRawResult;
import com.skincheck_backend.analysis.dto.AiAnalysisResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
@ConditionalOnProperty(name = "ai.provider", havingValue = "real")
@RequiredArgsConstructor
public class RealAiResultProvider implements AiResultProvider {

    private final WebClient.Builder webClientBuilder;

    @Value("${ai-server.base-url}")
    private String aiServerBaseUrl;

    /**
     * ✅ 기존 요약 분석 (유지)
     */
    @Override
    public AiAnalysisResponse analyze(String imageUrl) {
        WebClient webClient = webClientBuilder
                .baseUrl(aiServerBaseUrl)
                .build();

        return webClient.post()
                .uri("/ai/analyze")
                .bodyValue(Map.of("imageUrl", imageUrl))
                .retrieve()
                .bodyToMono(AiAnalysisResponse.class)
                .block();
    }

    /**
     * ⭐ 부위별 raw 분석 (신규)
     */
    @Override
    public AiAnalysisRawResult analyzeRaw(String imageUrl) {
        WebClient webClient = webClientBuilder
                .baseUrl(aiServerBaseUrl)
                .build();

        return webClient.post()
                .uri("/ai/analyze/raw")
                .bodyValue(Map.of("imageUrl", imageUrl))
                .retrieve()
                .bodyToMono(AiAnalysisRawResult.class)
                .block();
    }
}
